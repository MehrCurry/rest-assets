package com.vjoon.se.core.config;

import com.vjoon.se.core.services.FileStore;
import com.vjoon.se.core.services.LocalFileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class FileStoreConfig {
    @EndpointInject(uri="direct:production")
    private ProducerTemplate productionTemplate;

    @EndpointInject(uri="direct:mirror")
    private ProducerTemplate mirrorTemplate;

    @Value("${production.root}")
    private String productionRoot;

    @Value("${mirror.root}")
    private String mirrorRoot;

    @Bean(name = "production")
    public FileStore productionFileStore() {
        return new LocalFileStore(productionTemplate, productionRoot);
    }

    @Bean(name = "mirror")
    public FileStore mirrorFileStore() {
        return new LocalFileStore(mirrorTemplate, mirrorRoot);
    }

}
