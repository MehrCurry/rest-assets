package com.vjoon.se.core.config;

import com.vjoon.se.core.togglz.FileStoreFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.user.SimpleFeatureUser;

@Configuration
public class FileStoreFeatureConfig  {

    @Bean
    public FeatureManager featureManager() {
        return new FeatureManagerBuilder()
                .featureEnum(FileStoreFeature.class)
                .stateRepository(new InMemoryStateRepository())
                .userProvider(() -> new SimpleFeatureUser("admin", true))
                .build();
    }
}