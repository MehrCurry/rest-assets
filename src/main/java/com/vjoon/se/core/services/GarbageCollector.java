package com.vjoon.se.core.services;

import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
@Transactional
public class GarbageCollector {
    @Autowired
    private MediaRepository repository;

    @Autowired
    private FileStore fileStore;

    @Scheduled(fixedRate = 10000)
    public void deleteOrphanedAssets() {
        List<Media> result = repository.findByNotExistsInProductionAndSnapshotsIsEmpty();
        result.forEach(m -> {
            log.debug("Orphaned media: {}", m);
            Path p = Paths.get("mirror", fileStore.createFullNameFromID(m.getNameSpace(), m.getExternalReference()));
            log.debug("Path: {}", p);

            try {
                repository.delete(m);
                Files.deleteIfExists(p);
            } catch (IOException e) {
                throw new FileStoreException(e);
            }
        });
    }

}
