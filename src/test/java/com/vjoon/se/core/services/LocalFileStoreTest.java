package com.vjoon.se.core.services;

import com.vjoon.se.core.AssetRepositoryApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration @IntegrationTest("server.port:0") @ActiveProfiles("test") @Category(IntegrationTest.class)
public class LocalFileStoreTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private static final String FILE_KEY = "12345678";

    @Autowired private LocalFileStore fileStore;
    private static final String NAME_SPACE = "junit";
    private Path f;

    @Before public void setUp() throws Exception {
        fileStore.setBasePath("assets/test");
        f = Files.createTempFile("junit", ".txt");
        Files.write(f, "bla".getBytes());
    }

    @After public void tearDown() throws Exception {
        Files.delete(f);
        fileStore.deleteAll();
    }

    @Test public void testSave() throws Exception {
        fileStore.save(NAME_SPACE, FILE_KEY, Files.newInputStream(f), false);
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isTrue();
        fileStore.delete(NAME_SPACE, FILE_KEY);
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isFalse();
    }

    @Test public void testDuplicateFile() throws Exception {
        fileStore.save(NAME_SPACE, FILE_KEY, Files.newInputStream(f), false);
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isTrue();
        thrown.expect(IllegalStateException.class);
        fileStore.save(NAME_SPACE, FILE_KEY, Files.newInputStream(f), false);
    }

    @Test public void testGetStream() throws Exception {
        fileStore.save(NAME_SPACE, FILE_KEY, Files.newInputStream(f), false);
        assertThat(fileStore.exists(NAME_SPACE, FILE_KEY)).isTrue();
        InputStream is = fileStore.getStream(NAME_SPACE, FILE_KEY);
        assertThat(is).isNotNull();
    }
}
