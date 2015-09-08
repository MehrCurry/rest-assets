package com.vjoon.se.core.services;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class TestFileStoreConfig {
    @EndpointInject(uri="direct:test")
    private ProducerTemplate testTemplate;

    @Value("${test.root}")
    private String testRoot;

    @Bean(name = "test")
    public FileStore testFileStore() {
        return new LocalFileStore(testTemplate, testRoot);
    }
}
