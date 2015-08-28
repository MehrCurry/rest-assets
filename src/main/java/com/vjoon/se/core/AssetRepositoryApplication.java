package com.vjoon.se.core;

import com.google.common.base.Predicate;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.MultipartConfigElement;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

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
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(paths()).apis(
                RequestHandlerSelectors.any()).build();
    }

    private Predicate<String> paths() {
        return or(
                regex("/asset.*"),
                regex("/assets.*"),
                regex("/snapshot.*"),
                regex("/snapshots.*"),
                regex("/token.*"),
                regex("/tokens.*"));
    }
    @Bean
    public EventBus eventBus() {
        Executor executor= Executors.newFixedThreadPool(30);
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
