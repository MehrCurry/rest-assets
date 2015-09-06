package com.vjoon.se.core.control;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class SnapshotControllerTest {

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
    }

    @Test
    public void testRestore() throws IOException {
        uploadFile(f1,"junit",f1.toFile().getName());
        assertThat(assetController.findAll()).hasSize(1);
    }

    private void uploadFile(Path p,String namespace, String key) throws IOException {
        MultipartFile multipart = (MultipartFile) MultipartEntityBuilder.create()
                .addBinaryBody("file",p.toFile())
                .addTextBody("name",p.toFile().getName())
                .addTextBody("key",key)
                .addTextBody("namespace",namespace)
                .build();
        assetController.handleUpload(multipart,key,namespace,false);

    }
}