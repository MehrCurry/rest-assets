package com.vjoon.se.core.control;

import com.vjoon.se.core.AssetRepositoryApplication;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0") @ActiveProfiles("test") @Category(IntegrationTest.class)
public class SnapshotControllerITest {

    private Path f1,f2;

    @Autowired
    private AssetController assetController;

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
    public void testRestore() throws IOException {
        uploadFile(f1,"junit1");
        assertThat(assetController.findAll()).hasSize(1);
        uploadFile(f1,"junit2");
        assertThat(assetController.findAll()).hasSize(2);
        uploadFile(f2,"junit2");
        assertThat(assetController.findAll()).hasSize(3);
    }

    private void uploadFile(Path p,String namespace) throws IOException {
        File file = p.toFile();
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        assetController.handleUpload(multipartFile,file.getName(),namespace,false);

    }
}