package com.vjoon.se.core.boundary.rest;

import com.jayway.restassured.RestAssured;
import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.repository.AssetRepository;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by guido on 29.08.15.
 */
public class AbstracAssetResourceITest {
    protected Asset asset;
    @Value("${local.server.port}")
    private int port;
    @Autowired
    protected AssetRepository assetRepository;

    @Before
    public void setUp() {
        RestAssured.port=port;
        this.asset= Asset.builder()
                .originalFilename("junit.test")
                .contentType("text/plain")
                .externalReference("12345678")
                .nameSpace("test").build();
        assetRepository.save(asset);
    }

    @After
    public void tearDown() {
        assetRepository.deleteAll();
    }
}
