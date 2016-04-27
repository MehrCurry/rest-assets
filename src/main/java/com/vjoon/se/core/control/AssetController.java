package com.vjoon.se.core.control;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.HashFunction;
import com.google.common.hash.HashingInputStream;
import com.hazelcast.util.Base64;
import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.entity.NameSpace;
import com.vjoon.se.core.event.AssetCreatedEvent;
import com.vjoon.se.core.event.AssetDeletedEvent;
import com.vjoon.se.core.repository.AssetRepository;
import com.vjoon.se.core.services.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.tika.Tika;
import org.hibernate.cfg.NotYetImplementedException;
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
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
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

    @Autowired
    private HashFunction hashFunction;

    @EndpointInject
    private ProducerTemplate camel;

    public void handleUpload(MultipartFile multipart, String ref, String nameSpace, boolean overwrite) throws IOException {
        Stopwatch sw = Stopwatch.createStarted();
        checkNotNull(multipart);
        checkNotNull(nameSpace);
        NameSpace ns=new NameSpace(nameSpace);
        checkArgument(ns.isValid());
        checkNotNull(ref);

        try {
            final InputStream inputStream = multipart.getInputStream();
            final String originalFilename = multipart.getOriginalFilename();
            saveAsset(inputStream, ref, ns, originalFilename, overwrite);
            log.debug("Upload took {}",sw.toString());
        } catch (Exception e) {
            fileStore.delete(ns,ref);
            throw e;
        }
    }

    public void saveAsset(InputStream inputStream, String ref, NameSpace ns, String originalFilename, boolean overwrite) throws IOException {
        String hash;
        try (HashingInputStream multipartInputStream = new HashingInputStream(hashFunction, inputStream)) {
            fileStore.save(ns, ref, multipartInputStream, Optional.empty(), overwrite);
            hash=new String(Base64.encode(multipartInputStream.hash().asBytes()));
        }
        long size = fileStore.getSize(ns,ref);
        try (InputStream stream = fileStore.getStream(ns, ref)) {
            String contentType = new Tika().detect(stream);
            Asset media= Asset.builder()
                .length(size)
                .nameSpace(ns)
                .originalFilename(originalFilename)
                .contentType(contentType)
                .key(ref)
                .hash(hash)
                .existsInProduction(true)
                .build();
            repository.save(media);
            eventBus.post(new AssetCreatedEvent(media));
        } catch (Exception e) {
            fileStore.delete(ns,ref);
            throw e;
        }
    }

    public void delete(String id) {
        Optional<Asset> found = repository.findByMediaId(id).stream().findFirst();
        Asset m=found.orElseThrow(() -> new NoSuchElementException(id));
        deleteFromProduction(m);
    }

    public void deleteFromProduction(Asset m) {
        m.setExistsInProduction(false);
        m.setDeletedAt(new Date());
        repository.save(m);
        m.delete(fileStore);
    }

    public <R> void deleteAll() {
        List<Asset> assets = repository.findAll();
        List<Command> commands = assets.stream().map(a -> new DeleteCommand(a, fileStore)).collect(Collectors.toList());
        camel.asyncSendBody("direct:commands", commands);
    }

    public void deleteAllFromProduction(NameSpace namespace) {
        List<Asset> assets = repository.findByNameSpaceAndExistsInProduction(namespace, true);
        List<Command> commands = assets.stream().map(a -> new DeleteCommand(a, fileStore)).collect(Collectors.toList());
        camel.asyncSendBody("direct:commands", commands);
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

    public void handleUpload(InputStream inputStream, String name, String ref, String nameSpace, boolean b) {
        throw new NotYetImplementedException();
    }
}
