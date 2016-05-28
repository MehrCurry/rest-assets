package de.gzockoll.prototype.ams.boundary.rest;

import de.gzockoll.prototype.ams.AssetRepositoryApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
@Category(IntegrationTest.class)
@ImportResource("classpath:applicationContext.xml")
public class SnapshotResourceTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRestore() throws Exception {

    }
}