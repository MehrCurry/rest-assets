package com.vjoon.se.core.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalFileStoreTest {
    private static final String ROOT = "assets/test";
    @Rule public ExpectedException thrown = ExpectedException.none();


    private LocalFileStore fileStore;

    @Before public void setUp() throws Exception {
        fileStore=new LocalFileStore(null, ROOT);
    }

    @After public void tearDown() throws Exception {
        deleteAll(Paths.get(ROOT));
    }

    private void deleteAll(Path path) throws IOException {
        Files.walkFileTree(path, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test public void testDeleteEmptyParentDirectories() throws IOException {
        Path p=Files.createTempDirectory("junit");
        Path testPath=Paths.get(p.toString(), "1/2/3/4/5");
        Files.createDirectories(testPath);
        fileStore.deleteEmptyParentDirectories(testPath);
    }

    @Test public void testDeleteEmptyDirectory() throws IOException {
        Path p=Files.createTempDirectory("junit");
        Path testPath=p.resolve("test");
        Files.createDirectories(testPath);
        assertThat(Files.exists(testPath)).isTrue();
        fileStore.removeDirectoryIfEmpty(testPath);
        assertThat(Files.exists(testPath)).isFalse();
        Files.delete(p);
    }
}
