package de.gzockoll.prototype.assets.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.google.common.collect.ImmutableMap;
import com.hazelcast.util.Base64;
import de.gzockoll.prototype.assets.AssetRepositoryApplication;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@ImportResource("classpath:applicationContext.xml")
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
@Category(IntegrationTest.class)
public class S3FileStoreTest {
    @Autowired
    private AmazonS3 amazonS3;

    @EndpointInject(uri="direct:s3Upload")
    private ProducerTemplate template;

    @Before
    public void setUp() throws IOException {
        Files.write(Paths.get("duke.txt"), "Test".getBytes());
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("duke.txt"));
    }

    @Test
    @Ignore
    public void withTransferManager() throws InterruptedException, IOException {
        File file = new File("duke.txt");
        TransferManager transferManager = new TransferManager(this.amazonS3);
        UploadResult result = transferManager.upload("gzbundles", file.getName(), file)
                .waitForUploadResult();
        assertThat(result.getETag()).isNotEmpty();
        transferManager.shutdownNow();
    }

    @Test
    public void testCamelUpload() throws IOException {
        File file = new File("duke.txt");
        Map headers= ImmutableMap.of(
                S3Constants.KEY, file.getName(),
                S3Constants.CONTENT_LENGTH, file.length(),
                S3Constants.CONTENT_MD5, Base64.encode(DigestUtils.md5(new FileInputStream(file))));
        template.sendBody(file);
    }


}