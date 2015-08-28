package com.vjoon.se.core;

import com.google.common.eventbus.AsyncEventBus;
import javax.servlet.MultipartConfigElement;

import com.google.common.eventbus.EventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableSwagger2
public class AssetRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetRepositoryApplication.class, args);
    }

    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(PathSelectors.any()).apis(
                RequestHandlerSelectors.any()).build();
    }
    @Bean
    public EventBus eventBus() {
        Executor executor= Executors.newFixedThreadPool(10);
        return new AsyncEventBus(executor);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        final MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("5120MB");
        factory.setMaxRequestSize("5120MB");
        return factory.createMultipartConfig();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("StorageEngine API").description("API to store and download assets")
                .termsOfServiceUrl("Dead Link").contact("www.vjoon.com").license("Apache License Version 2.0")
                .version("2.0").build();
    }
}
