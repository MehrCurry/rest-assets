package de.gzockoll.prototype.ams.services;

import com.google.common.eventbus.EventBus;
import de.gzockoll.prototype.ams.entity.Asset;
import de.gzockoll.prototype.ams.event.AssetDeletedEvent;
import de.gzockoll.prototype.ams.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
public class GarbageCollector {
    @Autowired
    private AssetRepository repository;

    @Autowired
    private EventBus eventBus;

    @Scheduled(fixedRate = 60000)
    public void deleteOrphanedAssets() {
        List<Asset> result = repository.findByNotExistsInProductionAndSnapshotsIsEmpty();
        result.forEach(m -> {
            log.debug("Orphaned media: {}", m);
            repository.delete(m);
            eventBus.post(new AssetDeletedEvent(m));
        });
    }
}
