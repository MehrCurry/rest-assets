package com.vjoon.se.core.control;

import com.google.common.collect.ImmutableList;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.entity.NameSpace;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.pojo.TokenType;
import com.vjoon.se.core.repository.AssetRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenControllerTest {

    public static final String TEST_MAP = "tokens";
    public static final int TIME_TO_LIVE_SECONDS = 1;
    private TokenController controller;

    private AssetRepository repository;
    private Asset media;

    @Before public void setUp() {
        Config config =
                new Config().addMapConfig(new MapConfig().setName(TEST_MAP).setTimeToLiveSeconds(TIME_TO_LIVE_SECONDS));
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.getJoin().setMulticastConfig(new MulticastConfig().setEnabled(false));
        networkConfig.getJoin().setTcpIpConfig(new TcpIpConfig().setEnabled(false));
        config.setNetworkConfig(networkConfig);

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);

        controller = new TokenController();
        repository = mock(AssetRepository.class);
        controller.setRepository(repository);

        media = Asset.builder().nameSpace(new NameSpace("junit")).key("12345678").contentType("text/plain")
                .originalFilename("junit.txt").build();
        when(repository.findByMediaId(anyString())).thenReturn(ImmutableList.of(media));
        when(repository.findByNameSpaceAndKey(anyObject(), anyString())).thenReturn(ImmutableList.of(media));
        controller.setTokenMap(instance.getMap(TEST_MAP));
    }

    @After public void teadDown() {
        repository.delete(media);
    }

    @Test public void testToken() throws Exception {
        Token t = controller.createToken(media.getNameSpace(), media.getMediaId(), "DOWNLOAD", Optional.empty());
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
    }

    @Test public void testTokenByNamespaceAndKey() throws Exception {
        Token t = controller.createToken(media.getNameSpace(), media.getKey(), "DOWNLOAD",
                Optional.empty());
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
    }

    @Test public void testExpiredTokenWithGivenTTL() throws Exception {
        Token t = controller.createToken(media.getNameSpace(),media.getKey(), "DOWNLOAD",Optional.of(Long.valueOf(3)));
        Thread.sleep(2000);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
        Thread.sleep(1500);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }

    @Test public void testExpiredToken() throws Exception {
        Token t = controller.createToken(media.getNameSpace(), media.getMediaId(), "DOWNLOAD", Optional.empty());
        Thread.sleep(TIME_TO_LIVE_SECONDS * 1500);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }

    @Test public void testTokenType() {
        Token t = controller.createToken(media.getNameSpace(), media.getMediaId(), "DOWNLOAD", Optional.empty());
        assertThat(controller.getTokenFor(t.getId(), TokenType.DOWNLOAD).isPresent()).isTrue();
        assertThat(controller.getTokenFor(t.getId(), TokenType.UPLOAD).isPresent()).isFalse();
    }
}
