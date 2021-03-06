package de.gzockoll.prototype.ams.services;

import com.amazonaws.services.s3.AmazonS3;
import de.gzockoll.prototype.ams.AssetRepositoryApplication;
import de.gzockoll.prototype.ams.util.MediaIDGenerator;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
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
@Profile("s3Mirror")
public class S3FileStoreTest {

    private static final String BUCKET_NAME="gzbundles";
    public static final String MESSAGE = "test";

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    @Qualifier("s3")
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
        amazonS3.deleteObject(BUCKET_NAME, ID);
    }

    @Test
    public void testS3Stream() throws IOException {
        try (InputStream stream = fileStore.getStream("junit", "12345678")) {
            assertThat(stream).isNotNull();
            String readMessage= IOUtils.toString(stream, "UTF-8");
            assertThat(readMessage).isEqualTo(MESSAGE);
        }
    }

    @Test
    public void testExists() {
        assertThat(fileStore.exists("junit", "12345678")).isTrue();
    }

    @Test
    public void testNotExists() {
        assertThat(fileStore.exists("junit", "xxx12345678")).isFalse();
    }

    @Test
    public void testHash() {
        String hash=fileStore.getHash("junit", "12345678");
        assertThat(hash).isEqualTo("098f6bcd4621d373cade4e832627b4f6");
    }
}
