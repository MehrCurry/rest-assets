package com.vjoon.se.core.control;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.event.AssetCreatedEvent;
import com.vjoon.se.core.event.AssetDeletedEvent;
import com.vjoon.se.core.repository.AssetRepository;
import com.vjoon.se.core.services.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional
@Slf4j
public class AssetController {
    @Autowired
    private AssetRepository repository;

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
        multipart.getInputStream().close();
        try (InputStream stream = fileStore.getStream(nameSpace, ref)) {
            String contentType = new Tika().detect(stream);
            Asset media= Asset.builder()
                    .length(multipart.getSize())
                    .nameSpace(nameSpace)
                    .originalFilename(multipart.getOriginalFilename())
                    .contentType(contentType)
                    .externalReference(ref)
                    .hash(fileStore.getHash(nameSpace,ref))
                    .existsInProduction(true)
                    .build();
            repository.save(media);
            eventBus.post(new AssetCreatedEvent(media));
        }
    }

    public void delete(String id) {
        Optional<Asset> found = repository.findByMediaId(id).stream().findFirst();
        Asset m=found.orElseThrow(() -> new NoSuchElementException(id));
        deleteFromProduction(m);
    }

    private void deleteFromProduction(Asset m) {
        if (m.getSnapshots().isEmpty()) {
            repository.delete(m);
            eventBus.post(new AssetDeletedEvent(m));
        } else {
            m.setExistsInProduction(false);
            m.setDeletedAt(new Date());
            repository.save(m);
            m.delete(fileStore);
        }
    }

    public void deleteAll() {
        List<Asset> assets = repository.findAll();
        assets.forEach(m -> {
            deleteFromProduction(m);
        });
    }

    @Subscribe
    public void mediaDeleted(AssetDeletedEvent event) {
        event.getMedia().delete(fileStore);
    }

    public boolean assetExists(String assetID) {
        return !repository.findByMediaId(assetID).isEmpty();
    }

    public List<Asset> findAll() {
        return repository.findAll();
    }
}
