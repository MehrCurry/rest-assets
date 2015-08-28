package com.vjoon.se.core.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(paths()).apis(
                RequestHandlerSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("vjoon StorageEngine API").description("API to store and download assets")
                .termsOfServiceUrl("Dead Link").contact("www.vjoon.com").license("All rights reserved")
                .build();
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


}
