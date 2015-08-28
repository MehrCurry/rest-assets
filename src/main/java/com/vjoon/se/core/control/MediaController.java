package com.vjoon.se.core.control;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.event.MediaCreatedEvent;
import com.vjoon.se.core.event.MediaDeletedEvent;
import com.vjoon.se.core.repository.MediaRepository;
import com.vjoon.se.core.services.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional
@Slf4j
public class MediaController {
    @Autowired
    private MediaRepository repository;

    @Autowired
    private EventBus eventBus;

    @Autowired
    @Qualifier("production")
    private FileStore fileStore;

    public void handleUpload(MultipartFile multipart, String ref, String nameSpace, boolean overwrite) throws IOException {
        checkNotNull(multipart);
        checkNotNull(nameSpace);
        checkNotNull(ref);

        fileStore.save(nameSpace, ref, multipart.getInputStream(), Optional.empty(), overwrite);
        String contentType = new Tika().detect(fileStore.getStream(nameSpace, ref));
        Media media= Media.builder()
                .length(multipart.getSize())
                .contentType(multipart.getContentType())
                .nameSpace(nameSpace)
                .originalFilename(multipart.getOriginalFilename())
                .contentType(contentType)
                .length(multipart.getSize())
                .externalReference(ref)
                .hash(fileStore.getHash(nameSpace,ref))
                .existsInProduction(true)
                .build();
        repository.save(media);
        eventBus.post(new MediaCreatedEvent(media));
    }

    public void delete(String id) {
        Optional<Media> found = repository.findByMediaId(id).stream().findFirst();
        found.ifPresent(m -> {
            deleteFromProduction(m);
        });
    }

    private void deleteFromProduction(Media m) {
        m.setExistsInProduction(false);
        repository.save(m);
        m.delete(fileStore);
    }

    @Async
    public void deleteAll() {
        List<Media> assets = repository.findAll();
        assets.forEach(m -> {
            deleteFromProduction(m);
        });
    }

    @Subscribe
    public void mediaDeleted(MediaDeletedEvent event) {
        event.getMedia().delete(fileStore);
    }
}
