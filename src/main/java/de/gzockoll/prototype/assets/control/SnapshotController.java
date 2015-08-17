package de.gzockoll.prototype.assets.control;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import de.gzockoll.prototype.assets.entity.Snapshot;
import de.gzockoll.prototype.assets.repository.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SnapshotController {
    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private SnapshotRepository repository;

    public Snapshot create() {
        List<Media> assets = mediaRepository.findByExistsInProduction(true);
        Snapshot snapshot = new Snapshot(assets);
        return repository.save(snapshot);

    }

    public List<Snapshot> findAll() {
        return repository.findAll();
    }
}
