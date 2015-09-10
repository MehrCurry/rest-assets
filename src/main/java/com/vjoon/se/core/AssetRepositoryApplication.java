package com.vjoon.se.core;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.togglz.console.TogglzConsoleServlet;
import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;

import javax.servlet.MultipartConfigElement;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AssetRepositoryApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(AssetRepositoryApplication.class, args);
        FeatureManager featureManager = context.getBean(FeatureManager.class);
        StaticFeatureManagerProvider.setFeatureManager(featureManager);
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

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        return new ServletRegistrationBean(new TogglzConsoleServlet(),"/togglz/*");
    }
}
