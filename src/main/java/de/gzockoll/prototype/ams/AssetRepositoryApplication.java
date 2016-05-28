package de.gzockoll.prototype.ams;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.MultipartConfigElement;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AssetRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetRepositoryApplication.class, args);
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
    public HashFunction hashFunction() {
       return Hashing.adler32();
    }
}
