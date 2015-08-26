package com.vjoon.se.core.control;

import com.google.common.collect.ImmutableList;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenControllerTest {

    public static final String TEST_MAP = "tokens";
    public static final int TIME_TO_LIVE_SECONDS = 1;
    private TokenController controller;

    private MediaRepository repository;
    private Media media;

    @Before
    public void setUp() {
        System.setProperty("hazelcast.local.localAddress", "127.0.0.1");
        controller=new TokenController();
        repository=mock(MediaRepository.class);
        controller.setRepository(repository);

        media = Media.builder()
                .nameSpace("junit")
                .externalReference("12345678")
                .contentType("text/plain")
                .originalFilename("junit.txt")
                .build();
        when(repository.findByMediaId(anyString())).thenReturn(ImmutableList.of(media));
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(new Config().addMapConfig(new MapConfig().setName(
                TEST_MAP).setTimeToLiveSeconds(TIME_TO_LIVE_SECONDS)));
        controller.setTokenMap(instance.getMap(TEST_MAP));
    }

    @After
    public void teadDown() {
        repository.delete(media);
    }

    @Test
    @Category(SlowTest.class)
    public void testToken() throws Exception {
        Token t=controller.createToken(media.getMediaId(), "DOWNLOAD");
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
    }

    @Test
    @Category(SlowTest.class)
    public void testExpiredToken() throws Exception {
        Token t=controller.createToken(media.getMediaId(), "DOWNLOAD");
        Thread.sleep(TIME_TO_LIVE_SECONDS*1500);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }
}
