package com.vjoon.se.core.boundary.rest;

import com.hazelcast.core.IMap;
import com.vjoon.se.core.AssetRepositoryApplication;
import com.vjoon.se.core.categories.SlowTest;
import com.vjoon.se.core.pojo.Token;
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
        super.tearDown();;
        tokenMap.clear();
    }

    @Test
    public void testCreateToken() {
        assertThat(tokenMap).hasSize(0);
        given().
                queryParam("mediaId",asset.getMediaId()).
                queryParam("type","download").
        when().
            post("/tokens").
                then().
                statusCode(200);
        assertThat(tokenMap).hasSize(1);
        assertThat(tokenMap.values().stream().findFirst().get().getPayload()).isEqualTo(asset);
    }

    @Test
    @Category(SlowTest.class)
    public void testCreateTokenWithTTL() throws InterruptedException {
        assertThat(tokenMap).hasSize(0);
        given().
                queryParam("mediaId",asset.getMediaId()).
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
                queryParam("mediaId","Not Existing").
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
                queryParam("mediaId",asset.getMediaId()).
                queryParam("type","not valid").
                when().
                post("/tokens").
                then().
                statusCode(422);
    }
}