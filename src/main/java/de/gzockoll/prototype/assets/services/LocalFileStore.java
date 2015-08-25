package de.gzockoll.prototype.assets.services;

import de.gzockoll.prototype.assets.util.MediaIDGenerator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

@Service
@Slf4j
public class LocalFileStore implements FileStore {
    private static final String CAMLE_BASE="assets/";
    @Value("${base.path}")
    @Setter
    private String basePath;

    @EndpointInject(uri="direct:fileStore")
    private ProducerTemplate template;

    @Override
    public void save(@NotNull String nameSpace,
                     @NotNull String key,
                     @NotNull InputStream stream,
                     boolean overwrite) {
        checkNotNull(nameSpace);
        checkNotNull(key != null);
        checkNotNull(stream != null);
        checkState(overwrite || !exists(nameSpace,key),"File already existing");

        String filename=createFileNameFromID(nameSpace, key);
        template.sendBodyAndHeader(stream,"CamelFileName",filename.replaceFirst(CAMLE_BASE,""));
    }

    String createFileNameFromID(String nameSpace,String key) {
        checkArgument(key.length()>=8,"Key too short");
        String mediaID=MediaIDGenerator.generateID(nameSpace,key);
        String parts[] = mediaID.substring(0,8).split("(?<=\\G.{2})");
        return basePath + File.separator + nameSpace + File.separator + Arrays.stream(parts).collect(Collectors.joining(File.separator)) + File.separator + mediaID;
    }

    @Override
    public InputStream getStream(String nameSpace, String key) {
        try {
            return new FileInputStream(createFileNameFromID(nameSpace,key));
        } catch (FileNotFoundException e) {
            throw new FileStoreException(e);
        }
    }

    @Override
    public boolean exists(String nameSpace, String key) {
        return Files.exists(Paths.get(createFileNameFromID(nameSpace,key)));
    }

    @Override
    public void delete(String nameSpace, String key) {
        try {
            Files.delete(Paths.get(createFileNameFromID(nameSpace, key)));
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }

    @Override
    public void deleteAll() {
        Path dir=Paths.get(basePath);
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            log.debug("Error", e);
        }
    }

    @Override
    public String getHash(String nameSpace, String key) {
        checkState(exists(nameSpace,key));
        try {
            return DigestUtils.md5Hex(getStream(nameSpace,key));
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }
}
