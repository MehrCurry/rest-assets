package com.vjoon.se.core.control;

import com.vjoon.se.core.AssetRepositoryApplication;
import com.vjoon.se.core.services.FileStore;
import org.apache.commons.io.IOUtils;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0") @ActiveProfiles("test") @Category(IntegrationTest.class)
public class AssetControllerITest {
    @Rule
    public ExpectedException thrown=ExpectedException.none();

    private Path f1,f2;

    @Autowired
    private AssetController assetController;

    @Autowired
    @Qualifier("production")
    private FileStore fileStore;

    @Before
    public void setUp() throws Exception {
        f1 = Files.createTempFile("junit1", ".txt");
        Files.write(f1, "bla".getBytes());
        f2 = Files.createTempFile("junit2", ".txt");
        Files.write(f2, "foo".getBytes());
    }

    @After
    public void tearDown() throws Exception {
        assetController.deleteAll();
        Files.delete(f1);
        Files.delete(f2);
    }

    @Test
    public void testSuccessfulUpload() throws IOException {
        uploadFile(f1,"unittest", "testname");
        assertThat(assetController.findAll()).hasSize(1);
    }

    @Test
    public void testFailedUpload() throws IOException {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("namespace is not alphanumeric");
        try {
            uploadFile(f1, "unit/test","testname");
        } catch (ConstraintViolationException e) {
            assertThat(assetController.findAll()).hasSize(0);
            assertThat(fileStore.exists("unit/test","testname")).isFalse();
            throw e;
        }
    }

    private void uploadFile(Path p, String namespace, String key) throws IOException {
        File file = p.toFile();
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        assetController.handleUpload(multipartFile,key,namespace,false);

    }

}
