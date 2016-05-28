package de.gzockoll.prototype.ams.boundary.rest;

import de.gzockoll.prototype.ams.AssetRepositoryApplication;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;

import static com.jayway.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.http.HttpStatus.*;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
@Category(IntegrationTest.class)
@ImportResource("classpath:applicationContext.xml")
public class AssetResourceITest extends AbstracAssetResourceITest {

    @Test
    public void testThereAreNoAssets() {
        assetRepository.deleteAll();
        when().
            get("/assets").
                then().
                statusCode(200).
                body("$", equalTo(Collections.EMPTY_LIST));
    }

    @Test
    public void testGetAll() {
        when().
                get("/assets").
                then().
                statusCode(OK.value());
    }

    @Test
    public void testDeleteNonExistingAsset() {
        when().
                delete("/asset/1212").
                then().
                statusCode(NOT_FOUND.value());
    }

    @Test
    public void testDeleteExistingAsset() {
        when().
                delete("/asset/"+asset.getMediaId()).
                then().
                statusCode(NO_CONTENT.value());
        assertThat(assetRepository.findAll()).isEmpty();
    }
}