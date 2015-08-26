package com.vjoon.se.core.services;

import com.vjoon.se.core.util.MediaIDGenerator;
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
import java.util.function.Consumer;
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

        String filename=createFullNameFromID(nameSpace, key).replaceFirst(CAMLE_BASE, "");
        template.sendBodyAndHeader(stream,"CamelFileName",filename);
    }

    @Override
    public String createFileNameFromID(String nameSpace, String key) {
        checkArgument(key.length() >= 8, "Key too short");
        String mediaID= MediaIDGenerator.generateID(nameSpace, key);
        String parts[] = mediaID.substring(0, 8).split("(?<=\\G.{2})");
        return nameSpace + File.separator + Arrays.stream(parts).collect(Collectors.joining(File.separator)) + File.separator + mediaID;
    }

    public String createFullNameFromID(String nameSpace, String key) {
        return basePath + File.separator + createFileNameFromID(nameSpace, key);
    }

    @Override
    public InputStream getStream(String nameSpace, String key) {
        try {
            return new FileInputStream(createFullNameFromID(nameSpace,key));
        } catch (FileNotFoundException e) {
            throw new FileStoreException(e);
        }
    }

    @Override
    public boolean exists(String nameSpace, String key) {
        return Files.exists(Paths.get(createFullNameFromID(nameSpace,key)));
    }

    @Override
    public void delete(String nameSpace, String key) {
        Path path = Paths.get(createFullNameFromID(nameSpace, key));
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.warn("Problems while deleting file",e);
        }
        try {
            deleteEmptyParentDirectories(path);
        } catch (IOException e) {
            log.warn("Problems while deleting file",e);
        }
    }

    private void deleteEmptyParentDirectories(Path path) throws IOException {
        while ((path=path.getParent())!=null) {
            if (Files.isDirectory(path) && directoryIsEmpty(path)) {
                Files.deleteIfExists(path);
            }
        }
    }

    private boolean directoryIsEmpty(Path path) throws IOException {
        return !Files.list(path).findFirst().isPresent();
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
