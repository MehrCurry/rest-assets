package com.vjoon.se.core.control;

import com.google.common.collect.ImmutableMap;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.services.FileStoreException;
import com.vjoon.se.core.repository.MediaRepository;
import com.vjoon.se.core.services.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service @Transactional @Slf4j public class MediaController {

    @Autowired private MediaRepository repository;

    @EndpointInject(uri = "direct:s3tmp") private ProducerTemplate producerTemplate;

    @Autowired private FileStore fileStore;

    public void handleUpload(MultipartFile multipart, String ref, String nameSpace, boolean overwrite)
            throws IOException {
        checkNotNull(multipart);
        checkNotNull(nameSpace);
        checkNotNull(ref);

        fileStore.save(nameSpace, ref, multipart.getInputStream(), overwrite);
        String contentType = new Tika().detect(fileStore.getStream(nameSpace, ref));
        Media media = Media.builder().length(multipart.getSize()).nameSpace(nameSpace)
                .originalFilename(multipart.getOriginalFilename()).contentType(contentType).length(multipart.getSize())
                .externalReference(ref).hash(fileStore.getHash(nameSpace, ref)).existsInProduction(true).build();
        repository.save(media);
        sendToS3(media);
    }

    @Async public void sendToS3(Media media) {
        Map<String, Object> headers = ImmutableMap.of("CamelFileName", media.getMediaId(), "CamelAwsS3Headers",
                ImmutableMap.of("originalFilename", media.getOriginalFilename()));
        producerTemplate
                .sendBodyAndHeaders(fileStore.getStream(media.getNameSpace(), media.getExternalReference()), headers);
    }

    @Async public void deleteEmptyDirectories() throws IOException {
        Path p = Paths.get("assets/production");
        Files.walkFileTree(p, new SimpleFileVisitor<Path>() {

            @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (isDirEmpty(dir))
                    Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public void delete(String id) {
        Optional<Media> found = repository.findByMediaId(id).stream().findFirst();
        found.ifPresent(m -> {
            fileStore.delete(m.getNameSpace(), m.getExternalReference());
            repository.delete(m);
            try {
                deleteEmptyDirectories();
            } catch (IOException e) {
                log.warn("Delete Directories: ", e);
            }
        });
    }

    @Async public void deleteAll() {
        repository.findAll().forEach(m -> {
            try {
                fileStore.delete(m.getNameSpace(), m.getExternalReference());
            } catch (FileStoreException e) {
                log.warn("Error deleting file:", e);
            }
        });
        repository.deleteAll();
        try {
            deleteEmptyDirectories();
        } catch (IOException e) {
            log.warn("Delete Directories: ", e);
        }
    }

    private static boolean isDirEmpty(final Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    public List<Media> getAll() {
        return repository.findAll();
    }

}
