package de.gzockoll.prototype.ams.camel;

import org.apache.camel.Exchange;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.stereotype.Service;

import java.io.File;

@Service public class MultipartCreator {

    public HttpEntity createEntity(Exchange ex) {
        File file = ex.getIn().getBody(File.class);
        final String key = (String) ex.getIn().getHeader("key");
        final String namespace = (String) ex.getIn().getHeader("namespace");

        String name = file.getName();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file);
        builder.addTextBody("name", name);
        if (key != null)
            builder.addTextBody("key", key);
        if (namespace != null)
            builder.addTextBody("namespace", namespace);
        return builder.build();
    }
}
