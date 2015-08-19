package de.gzockoll.prototype.assets.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import de.gzockoll.prototype.assets.pojo.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance(config());
    }

    @Bean
    public Config config() {
        return new Config().addMapConfig(mapConfig());

    }

    @Bean
    public MapConfig mapConfig() {
        return new MapConfig()
                .setName("tokens")
                .setTimeToLiveSeconds(60);
    }

    @Bean(name = "tokens")
    public IMap tokensMap() {
        return hazelcastInstance().getMap("tokens");
    }
}
