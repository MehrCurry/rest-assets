package com.vjoon.se.core.control;

import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.entity.Snapshot;
import com.vjoon.se.core.repository.MediaRepository;
import com.vjoon.se.core.repository.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Transactional public class SnapshotController {

    @Autowired private MediaRepository mediaRepository;

    @Autowired private SnapshotRepository repository;

    public Snapshot create() {
        List<Media> assets = mediaRepository.findByExistsInProduction(true);
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
}
