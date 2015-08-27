package com.vjoon.se.core.services;

import com.amazonaws.services.s3.AmazonS3;
import com.vjoon.se.core.AssetRepositoryApplication;
import com.vjoon.se.core.util.MediaIDGenerator;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0") @ActiveProfiles("test") @Category(IntegrationTest.class)
@ImportResource("classpath:applicationContext.xml")
public class S3FileStoreTest {

    private static final String BUCKET_NAME="gzbundles";
    public static final String MESSAGE = "test";

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private S3FileStore fileStore;
    private static final String ID = MediaIDGenerator.generateID("junit", "12345678");

    public void writeResource() throws IOException {
        Resource resource = this.resourceLoader.getResource("s3://" + BUCKET_NAME + "/" + ID);

        WritableResource writableResource = (WritableResource) resource;
        try (OutputStream outputStream = writableResource.getOutputStream()) {
            outputStream.write(MESSAGE.getBytes());
        }
    }

    @Before
    public void setUp() throws Exception {
        writeResource();
    }

    @After
    public void tearDown() throws Exception {
        amazonS3.deleteObject(BUCKET_NAME,ID);
    }

    @Test
    // @Ignore
    public void testS3Stream() throws IOException {
        InputStream stream = fileStore.getStream("junit", "12345678");
        assertThat(stream).isNotNull();
        String readMessage= IOUtils.toString(stream, "UTF-8");
        assertThat(readMessage).isEqualTo(MESSAGE);
    }
}
