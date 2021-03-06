package de.gzockoll.prototype.ams.boundary.rest;

import com.hazelcast.core.IMap;
import de.gzockoll.prototype.ams.AssetRepositoryApplication;
import de.gzockoll.prototype.ams.categories.SlowTest;
import de.gzockoll.prototype.ams.pojo.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
@Category(IntegrationTest.class)
@ImportResource("classpath:applicationContext.xml")
public class TokenResourceITest extends AbstracAssetResourceITest {

    @Resource(name = "tokens")
    private IMap<String, Token> tokenMap;

    @Before
    public void setUp() {
        super.setUp();
        tokenMap.clear();
    }

    @After
    public void tearDown() {
        super.tearDown();
        tokenMap.clear();
    }

    @Test
    public void testCreateToken() {
        assertThat(tokenMap).hasSize(0);
        given().
                queryParam("namespace",asset.getNameSpace()).
                queryParam("key",asset.getKey()).
                queryParam("type", "download").
        when().
            post("/tokens").
                then().
                statusCode(200);
        assertThat(tokenMap).hasSize(1);
        assertThat(tokenMap.values().stream().findFirst().get().getPayload()).isEqualTo(asset);
    }

    @Test
    @Category(SlowTest.class)
    @Ignore
    public void testCreateTokenWithTTL() throws InterruptedException {
        assertThat(tokenMap).hasSize(0);
        given().
                queryParam("namespace", asset.getNameSpace()).
                queryParam("key",asset.getKey()).
                queryParam("type","download").
                queryParam("ttl",3).
                when().
                post("/tokens").
                then().
                statusCode(200);
        assertThat(tokenMap).hasSize(1);
        Thread.sleep(5000);
        assertThat(tokenMap).hasSize(0);
    }

    @Test
    public void testCreateTokenWithInvalidMediaID() {
        assertThat(tokenMap).hasSize(0);
        given().
                queryParam("namespace", asset.getNameSpace()).
                queryParam("key","not existing").
                queryParam("type","download").
                when().
                post("/tokens").
                then().
                statusCode(404);
    }

    @Test
    public void testCreateTokenWithInvalidType() {
        assertThat(tokenMap).hasSize(0);
        given().
                queryParam("namespace",asset.getNameSpace()).
                queryParam("key",asset.getKey()).
                queryParam("type","not valid").
                when().
                post("/tokens").
                then().
                statusCode(422);
    }
}
