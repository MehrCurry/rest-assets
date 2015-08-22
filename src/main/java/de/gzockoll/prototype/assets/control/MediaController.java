package de.gzockoll.prototype.assets.control;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@Transactional
@Slf4j
public class MediaController {
    @Autowired
    private MediaRepository repository;

    @EndpointInject(uri="direct:production")
    private ProducerTemplate producerTemplate;



    public void handleUpload(MultipartFile multipart, String ref, String nameSpace) throws IOException {
        checkArgument(multipart!=null);
        checkArgument(nameSpace!=null);

        Media media=Media.builder()
                .length(multipart.getSize())
                .contentType(multipart.getContentType())
                .nameSpace(nameSpace)
                .originalFilename(multipart.getOriginalFilename())
                .build();
        media.setExternalReference(ref);
        File convFile = new File(media.getFullname());
        convFile.getParentFile().mkdirs();
        multipart.transferTo(convFile.getAbsoluteFile());
        media.extractInfosFromFile(convFile);
        media.setExistsInProduction(true);
        repository.save(media);
    }

    @Async
    public void deleteEmptyDirectories() throws IOException {
        Path p= Paths.get("assets/production");
        Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (Files.list(dir).count() == 0)
                    Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public void delete(String id) {
        Optional<Media> found = repository.findByMediaId(id).stream().findFirst();
        found.ifPresent(m -> {
            repository.delete(m);
            try {
                deleteEmptyDirectories();
            } catch (IOException e) {
                log.warn("Delete Directories: ", e);
            }
        });
    }

    public void deleteAll() {
        repository.deleteAll();
        try {
            deleteEmptyDirectories();
        } catch (IOException e) {
            log.warn("Delete Directories: ", e);
        }
    }
}
