package de.gzockoll.prototype.ams.control;

import de.gzockoll.prototype.ams.entity.Asset;
import de.gzockoll.prototype.ams.entity.Snapshot;
import de.gzockoll.prototype.ams.repository.AssetRepository;
import de.gzockoll.prototype.ams.repository.SnapshotRepository;
import de.gzockoll.prototype.ams.services.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

@Service @Transactional public class SnapshotController {

    @Autowired private AssetRepository assetRepository;

    @Autowired private SnapshotRepository repository;

    @Autowired
    private AssetController assetController;

    @Autowired
    @Qualifier("production")
    private FileStore productionFileStore;

    @Autowired(required = false)
    @Qualifier("mirror")
    private FileStore mirrorFileStore;

    @Autowired(required = false)
    @Qualifier("s3")
    private FileStore s3FileStore;

    public Snapshot create(String namespace) {
        List<Asset> assets = assetRepository.findByNameSpaceAndExistsInProduction(namespace, true).collect(Collectors.toList());
        Snapshot snapshot = new Snapshot(namespace,assets);
        return repository.save(snapshot);
    }

    public List<Snapshot> findAll(String namespace) {
        return repository.findByNamespace(namespace).collect(Collectors.toList());
    }

    public void deleteAll(String namespace) {
        repository.findByNamespace(namespace).forEach(s -> {
            s.getIncluded().forEach(a -> {
                a.remove(s);
                assetRepository.save(a);
            });
            repository.delete(s);
        });
    }

    public void delete(Long id) {
        Snapshot s=repository.findOne(id);
        s.getIncluded().forEach(m -> m.remove(s));
        repository.delete(s);
    }

    public Snapshot restore(Long id, boolean restoreSnapshot) {
        Snapshot snapshot=repository.findOne(id);
        if (snapshot==null)
            throw new NoSuchElementException();
        if (restoreSnapshot) {
            restoreFiles(snapshot);
        }
        return snapshot;
    }

    public void restoreFiles(Snapshot snapshot) {
        assetController.deleteAllFromProduction(snapshot.getNamespace());

        snapshot.getIncluded()
                .stream()
                .sorted((a1, a2) -> Long.compare(a1.getLength(), a2.getLength()))
                .forEach(a -> {
                    a.copy(getMirrorFileStore(), productionFileStore);
                    a.setExistsInProduction(true);
                    assetRepository.save(a);
                });
    }

    public FileStore getMirrorFileStore() {
        checkState((mirrorFileStore != null || s3FileStore != null), "At least one filestore must be available");
        return (mirrorFileStore != null) ? mirrorFileStore : s3FileStore;
    }
}
