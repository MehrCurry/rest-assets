package com.vjoon.se.core.boundary.rest;

import com.jayway.restassured.RestAssured;
import com.vjoon.se.core.AssetRepositoryApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
@Category(IntegrationTest.class)
@ImportResource("classpath:applicationContext.xml")
public class AssetResourceIT {
    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() {
        RestAssured.port=port;
    }

    @Test
    public void testRest() {
        given().
        when().
            get("/assets").
                then().
                statusCode(200).
                body("$", equalTo(Collections.EMPTY_LIST));
    }
}