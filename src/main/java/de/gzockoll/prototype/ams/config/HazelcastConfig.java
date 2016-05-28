package de.gzockoll.prototype.ams.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration public class HazelcastConfig {

    @Bean public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance(config());
    }

    @Bean public Config config() {
        return new Config().addMapConfig(mapConfig()).setNetworkConfig(networkConfig());

    }

    @Bean public MapConfig mapConfig() {
        return new MapConfig().setName("tokens").setTimeToLiveSeconds(60);
    }

    @Bean(name = "tokens") public IMap tokensMap() {
        return hazelcastInstance().getMap("tokens");
    }

    @Bean public NetworkConfig networkConfig() {
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.getJoin().setMulticastConfig(new MulticastConfig().setEnabled(false));
        networkConfig.getJoin().setTcpIpConfig(new TcpIpConfig().setEnabled(false));
        return networkConfig;
    }
}
