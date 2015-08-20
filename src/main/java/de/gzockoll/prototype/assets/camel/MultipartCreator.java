package de.gzockoll.prototype.assets.camel;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MultipartCreator {
    public HttpEntity createEntity(File file) {
            String name= file.getName();
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file);
            builder.addTextBody("name", name);
            return builder.build();
        }
}
