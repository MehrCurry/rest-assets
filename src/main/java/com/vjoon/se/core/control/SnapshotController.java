package com.vjoon.se.core.control;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.entity.Snapshot;
import com.vjoon.se.core.repository.AssetRepository;
import com.vjoon.se.core.repository.SnapshotRepository;
import com.vjoon.se.core.services.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

@Service @Transactional public class SnapshotController {

    @Autowired private AssetRepository assetRepository;

    @Autowired private SnapshotRepository repository;

    @Autowired
    private AssetController assetController;

    @Autowired
    @Qualifier("production")
    private FileStore productionFileStore;

    @Autowired
    @Qualifier("mirror")
    private FileStore mirrorFileStore;

    public Snapshot create() {
        List<Asset> assets = assetRepository.findByExistsInProduction(true);
        Snapshot snapshot = new Snapshot(assets);
        return repository.save(snapshot);
    }

    public List<Snapshot> findAll() {
        return repository.findAll();
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void delete(Long id) {
        Snapshot s=repository.findOne(id);
        s.getIncluded().forEach(m -> m.remove(s));
        repository.delete(s);
    }

    public Snapshot restore(Long id, boolean restoreSnapshot) {
        Snapshot snapshot=repository.findOne(id);
        checkState(snapshot != null);
        if (restoreSnapshot) {
            create();
            assetController.deleteAll();

            snapshot.getIncluded().forEach(a -> {
                a.copy(mirrorFileStore, productionFileStore);
                a.setExistsInProduction(true);
                assetRepository.save(a);
            });
        }
        return snapshot;
    }
}
