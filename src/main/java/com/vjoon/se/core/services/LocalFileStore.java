package com.vjoon.se.core.services;

import com.google.common.collect.ImmutableMap;
import com.vjoon.se.core.util.MediaIDGenerator;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

@Slf4j
public class LocalFileStore implements FileStore {
    @Setter(AccessLevel.PACKAGE)
    private String root;

    private ProducerTemplate template;

    public LocalFileStore(ProducerTemplate producerTemplate, String root) {
        this.template=producerTemplate;
        this.root=root;
    }

    @Override
    public void save(@NotNull String nameSpace,
                     @NotNull String key,
                     @NotNull InputStream stream,
                     Optional<String> checksum,
                     boolean overwrite) {
        checkNotNull(nameSpace);
        checkNotNull(key != null);
        checkNotNull(stream != null);
        if (!overwrite && exists(nameSpace,key)) {
            throw new DuplicateKeyException(String.format("File already existing: %s/%s",nameSpace,key));
        }
        String filename=createFullNameFromID(nameSpace, key).replaceFirst(root, "");
        Map<String,Object> headers= ImmutableMap.of(
                "CamelFileName", filename,
                "Checksum", checksum.orElse(""));

        template.sendBodyAndHeaders(stream,headers);
    }

    @Override
    public String createFileNameFromID(String nameSpace, String key) {
        checkArgument(key.length() >= 8, "Key too short");
        String mediaID= MediaIDGenerator.generateID(nameSpace, key);
        String parts[] = mediaID.substring(0, 8).split("(?<=\\G.{2})");
        return nameSpace + File.separator + Arrays.stream(parts).collect(Collectors.joining(File.separator)) + File.separator + mediaID;
    }

    @Override public String createFullNameFromID(String nameSpace, String key) {
        return root + File.separator + createFileNameFromID(nameSpace, key);
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
            deleteEmptyParentDirectories(path.getParent());
        } catch (IOException e) {
            log.warn("Problems while deleting file",e);
        }
    }

    void deleteEmptyParentDirectories(Path dir) throws IOException {
        checkArgument(Files.isDirectory(dir),"Path must be a directory");
        if (Files.isDirectory(dir) && directoryIsEmpty(dir)) {
            removeDirectoryIfEmpty(dir);
            deleteEmptyParentDirectories(dir.getParent());
        }
    }

    void removeDirectoryIfEmpty(Path dir) throws IOException {
        checkArgument(Files.isDirectory(dir),"Path must be a directory");
        Files.deleteIfExists(dir.resolve(".DS_Store"));
        if (directoryIsEmpty(dir)) {
            Files.deleteIfExists(dir);
        }
    }

    private boolean directoryIsEmpty(Path path) throws IOException {
        return Files.list(path).count()==0;
    }

    @Override
    public void deleteAll() {
        Path dir=Paths.get(root);
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
        checkState(exists(nameSpace, key));
        try (InputStream stream = getStream(nameSpace, key)){
            return DigestUtils.md5Hex(stream);
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }
    @Override
    public long getSize(String nameSpace, String key) {
        return new File(createFullNameFromID(nameSpace,key)).length();
    }
}
