package de.gzockoll.prototype.ams.config;

import de.gzockoll.prototype.ams.services.FileStore;
import de.gzockoll.prototype.ams.services.LocalFileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class FileStoreConfig {
    @EndpointInject(uri="direct:saveToFile")
    private ProducerTemplate template;

    @Value("${production.root}")
    private String productionRoot;

    @Value("${mirror.root}")
    private String mirrorRoot;

    @Bean(name = "production")
    public FileStore productionFileStore() {
        return new LocalFileStore(template, productionRoot);
    }

    @Bean(name = "mirror")
    @Profile("localMirror")
    public FileStore mirrorFileStore() {
        return new LocalFileStore(template, mirrorRoot);
    }

}
