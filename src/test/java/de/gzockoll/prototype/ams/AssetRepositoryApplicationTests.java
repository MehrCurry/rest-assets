package de.gzockoll.prototype.ams;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration @IntegrationTest("server.port:0") @ActiveProfiles("test") @Category(IntegrationTest.class)
public class AssetRepositoryApplicationTests {

    @Test public void contextLoads() {
    }

}
