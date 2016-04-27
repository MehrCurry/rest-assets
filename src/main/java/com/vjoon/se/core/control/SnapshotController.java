package com.vjoon.se.core.control;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.entity.NameSpace;
import com.vjoon.se.core.entity.Snapshot;
import com.vjoon.se.core.repository.AssetRepository;
import com.vjoon.se.core.repository.SnapshotRepository;
import com.vjoon.se.core.services.FileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

@Service @Transactional public class SnapshotController {
    private ExecutorService executor = Executors.newFixedThreadPool(3);

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

    @EndpointInject(uri = "direct:copy")
    private ProducerTemplate producer;

    public Snapshot create(NameSpace namespace) {
        List<Asset> assets = assetRepository.findByNameSpaceAndExistsInProduction(namespace, true);
        Snapshot snapshot = new Snapshot(namespace,assets);
        return repository.save(snapshot);
    }

    public List<Snapshot> findAll(NameSpace namespace) {
        return repository.findByNamespace(namespace);
    }

    public void deleteAll(NameSpace namespace) {
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
        List<CopyCommand> commands = snapshot.getIncluded().stream().map(a -> new CopyCommand(a, getMirrorFileStore(), productionFileStore)).collect(Collectors.toList());
        producer.asyncSendBody("direct:commands",commands);

        snapshot.getIncluded()
                .stream()
                .sorted((a1, a2) -> Long.compare(a1.getLength(), a2.getLength()))
                .forEach(a -> {
                    a.setExistsInProduction(true);
                    assetRepository.save(a);
                });
    }

    private void copy(Asset a, FileStore from, FileStore to) {
        producer.asyncSendBody("direct:copy",new CopyCommand(a,from,to));
    }


    public FileStore getMirrorFileStore() {
        checkState((mirrorFileStore != null || s3FileStore != null), "At least one filestore must be available");
        return (mirrorFileStore != null) ? mirrorFileStore : s3FileStore;
    }
}
