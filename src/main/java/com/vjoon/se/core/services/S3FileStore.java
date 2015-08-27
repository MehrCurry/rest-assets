package com.vjoon.se.core.services;

import com.google.common.collect.ImmutableMap;
import com.vjoon.se.core.util.MediaIDGenerator;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.*;

@Service("s3")
public class S3FileStore implements FileStore {
    private static final String BUCKET_NAME="gzbundles";

    @Autowired
    private ResourceLoader resourceLoader;

    @EndpointInject
    private ProducerTemplate producerTemplate;


    @Override
    public void save(String nameSpace, String key, InputStream stream, Optional<String> checksum, boolean overwrite) {
        checkNotNull(nameSpace);
        checkNotNull(key != null);
        checkNotNull(stream != null);
        checkState(overwrite || !exists(nameSpace,key),"File already existing");

        Map<String,Object> headers= ImmutableMap.of(
                "CamelFileName", createFileNameFromID(nameSpace, key),
                "Checksum", checksum
        );
        producerTemplate.sendBodyAndHeaders("direct:s3tmp", stream, headers);
    }

    @Override
    public String createFileNameFromID(String nameSpace, String key) {
        checkArgument(key.length() >= 8, "Key too short");
        String mediaID= MediaIDGenerator.generateID(nameSpace, key);
        return nameSpace + File.separator  + mediaID;
    }

    @Override
    public String createFullNameFromID(String nameSpace, String key) {
        return createFileNameFromID(nameSpace,key);
    }

    @Override
    public InputStream getStream(String namespace, String key) {
        try {
            return resourceLoader.getResource("s3://" + BUCKET_NAME + "/" + createFileNameFromID(namespace,key)).getInputStream();
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }

    @Override
    public boolean exists(String nameSpace, String key) {
        return false;
    }

    @Override
    public void delete(String nameSpace, String key) {
        throw new NotYetImplementedException();

    }

    @Override
    public void deleteAll() {
        throw new NotYetImplementedException();
    }

    @Override
    public String getHash(String nameSpace, String key) {
        throw new NotYetImplementedException();
    }
}
