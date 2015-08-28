package com.vjoon.se.core.config;

import com.vjoon.se.core.services.FileStore;
import com.vjoon.se.core.services.LocalFileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class FileStoreConfig {
    @EndpointInject
    private ProducerTemplate producerTemplate;

    @Bean(name = "production")
    public FileStore productionFileStore() {
        return new LocalFileStore("production",producerTemplate);
    }

    @Bean(name = "mirror")
    public FileStore mirrorFileStore() {
        return new LocalFileStore("mirror",producerTemplate);
    }

}
