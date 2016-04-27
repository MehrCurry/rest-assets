package com.vjoon.se.core.services;

import com.vjoon.se.core.AssetRepositoryApplication;
import com.vjoon.se.core.entity.NameSpace;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration @IntegrationTest("server.port:0") @ActiveProfiles("test") @Category(IntegrationTest.class)
public class LocalFileStoreITest {
    private static final NameSpace NAME_SPACE = new NameSpace("junit");
    private static final String FILE_KEY = "12345678";

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Autowired
    @Qualifier("test")
    private LocalFileStore fileStore;
    private Path f;
    private Path tmpDir;

    @Before public void setUp() throws Exception {
        tmpDir=Files.createTempDirectory("junit");
        fileStore.setRoot(tmpDir.toString());
        f = Files.createTempFile("junit", ".txt");
        Files.write(f, "bla" .getBytes());
    }

    @After public void tearDown() throws Exception {
        Files.delete(f);
        deleteAll(tmpDir);
    }

    @Test public void testSave() throws Exception {
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isFalse();
        try (final InputStream stream = Files.newInputStream(f)) {
            fileStore.save(NAME_SPACE, FILE_KEY, stream, Optional.empty(), false);
        }
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isTrue();
        fileStore.delete(NAME_SPACE, FILE_KEY);
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isFalse();
    }

    @Test public void testDuplicateFile() throws Exception {
        try (final InputStream stream = Files.newInputStream(f)) {
            fileStore.save(NAME_SPACE, FILE_KEY, stream, Optional.empty(), false);
        }
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isTrue();
        thrown.expect(DuplicateKeyException.class);
        fileStore.save(NAME_SPACE, FILE_KEY, Files.newInputStream(f), Optional.empty(), false);
    }

    @Test public void testGetStream() throws Exception {
        try (final InputStream stream = Files.newInputStream(f)) {
            fileStore.save(NAME_SPACE, FILE_KEY, stream, Optional.empty(), false);
        }
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isTrue();
        try (InputStream is = fileStore.getStream(NAME_SPACE, FILE_KEY)) {
            assertThat(is).isNotNull();
        };
    }

    @Test
    public void testSize() throws IOException {
        Path p=Files.createTempFile("junit",".txt");
        Files.write(p, "bla".getBytes());
        NameSpace junit = new NameSpace("junit");
        try (InputStream stream = Files.newInputStream(p)) {
            fileStore.save(junit, "12345678", stream, Optional.empty(),false);
        }
        Files.delete(p);
        assertThat(fileStore.getSize(junit, "12345678")).isEqualTo(3);
    }

    private void deleteAll(Path dir) {
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
            fail("Error", e);
        }
    }


}
