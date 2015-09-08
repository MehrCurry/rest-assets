package com.vjoon.se.core.control;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.entity.Snapshot;
import com.vjoon.se.core.repository.AssetRepository;
import com.vjoon.se.core.repository.SnapshotRepository;
import com.vjoon.se.core.services.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

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

    public Snapshot create() {
        List<Asset> assets = assetRepository.findByExistsInProduction(true);
        Snapshot snapshot = new Snapshot(assets);
        return repository.save(snapshot);
    }

    public List<Snapshot> findAll() {
        return repository.findAll();
    }

    public void deleteAll() {
        repository.findAll().forEach(s -> {
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
        assetController.deleteAllFromProduction();

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
        checkState((mirrorFileStore!=null || s3FileStore!=null),"At least one filestore must be available");
        return (mirrorFileStore != null) ? mirrorFileStore : s3FileStore;
    }
}
