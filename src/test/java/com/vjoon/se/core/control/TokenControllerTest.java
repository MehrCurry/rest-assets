package com.vjoon.se.core.control;

import com.vjoon.se.core.categories.SlowTest;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.repository.MediaRepository;
import com.vjoon.se.core.AssetRepositoryApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
@Category(IntegrationTest.class)
public class TokenControllerTest {
    @Autowired
    private TokenController controller;

    @Autowired
    private MediaRepository repository;
    private Media media;

    @Before
    public void setUp() {
        media = Media.builder()
                .nameSpace("junit")
                .externalReference("12345678")
                .contentType("text/plain")
                .originalFilename("junit.txt")
                .build();
        repository.save(media);
    }

    @After
    public void teadDown() {
        repository.delete(media);
    }

    @Test
    @Category(SlowTest.class)
    public void testExpireToken() throws Exception {
        Token t=controller.createToken(media.getMediaId());
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
        Thread.sleep(65000);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }
}
