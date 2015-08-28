package com.vjoon.se.core.services;

import com.google.common.eventbus.EventBus;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.event.MediaDeletedEvent;
import com.vjoon.se.core.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
public class GarbageCollector {
    @Autowired
    private MediaRepository repository;

    @Autowired
    private EventBus eventBus;

    @Autowired
    @Qualifier("production")
    private FileStore fileStore;

    @Scheduled(fixedRate = 60000)
    public void deleteOrphanedAssets() {
        List<Media> result = repository.findByNotExistsInProductionAndSnapshotsIsEmpty();
        result.forEach(m -> {
            log.debug("Orphaned media: {}", m);
            repository.delete(m);
            eventBus.post(new MediaDeletedEvent(m));
        });
    }
}
