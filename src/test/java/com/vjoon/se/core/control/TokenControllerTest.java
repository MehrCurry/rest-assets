package com.vjoon.se.core.control;

import com.google.common.collect.ImmutableList;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.vjoon.se.core.categories.SlowTest;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.pojo.TokenType;
import com.vjoon.se.core.repository.MediaRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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

    @Before public void setUp() {
        Config config =
                new Config().addMapConfig(new MapConfig().setName(TEST_MAP).setTimeToLiveSeconds(TIME_TO_LIVE_SECONDS));
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.getJoin().setMulticastConfig(new MulticastConfig().setEnabled(false));
        networkConfig.getJoin().setTcpIpConfig(new TcpIpConfig().setEnabled(false));
        config.setNetworkConfig(networkConfig);

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);

        controller = new TokenController();
        repository = mock(MediaRepository.class);
        controller.setRepository(repository);

        media = Media.builder().nameSpace("junit").externalReference("12345678").contentType("text/plain")
                .originalFilename("junit.txt").build();
        when(repository.findByMediaId(anyString())).thenReturn(ImmutableList.of(media));
        controller.setTokenMap(instance.getMap(TEST_MAP));
    }

    @After public void teadDown() {
        repository.delete(media);
    }

    @Test public void testToken() throws Exception {
        Token t = controller.createToken(media.getMediaId(), "DOWNLOAD");
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
    }

    @Test public void testExpiredToken() throws Exception {
        Token t = controller.createToken(media.getMediaId(), "DOWNLOAD");
        Thread.sleep(TIME_TO_LIVE_SECONDS * 1500);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }

    @Test public void testTokenType() {
        Token t = controller.createToken(media.getMediaId(), "DOWNLOAD");
        assertThat(controller.getTokenFor(t.getId(), TokenType.DOWNLOAD).isPresent()).isTrue();
        assertThat(controller.getTokenFor(t.getId(), TokenType.UPLOAD).isPresent()).isFalse();
    }
}
