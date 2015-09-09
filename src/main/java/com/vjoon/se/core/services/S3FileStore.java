package com.vjoon.se.core.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.common.collect.ImmutableMap;
import com.vjoon.se.core.util.MediaIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.*;

@Service("s3")
@Slf4j
public class S3FileStore implements FileStore {

    public static final String S3_QUEUE = "s3queue";
    private static final String BUCKET_NAME="gzbundles";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AmazonS3 amazonS3;


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
                "Checksum", checksum.get()
        );
        producerTemplate.sendBodyAndHeaders("direct:" + S3_QUEUE, stream, headers);
    }

    @Override
    public String createFileNameFromID(String nameSpace, String key) {
        checkArgument(key.length() >= 8, "Key too short");
        return MediaIDGenerator.generateID(nameSpace, key);
    }

    @Override
    public String createFullNameFromID(String nameSpace, String key) {
        return createFileNameFromID(nameSpace,key);
    }

    @Override
    public InputStream getStream(String namespace, String key) {
        try {
            return getResource(namespace, key).getInputStream();
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }

    @Override
    public boolean exists(String nameSpace, String key) {
        return getMetaData(nameSpace,key).isPresent();
    }

    private Optional<ObjectMetadata> getMetaData(String nameSpace, String key) {
        try {
            return Optional.of(amazonS3.getObjectMetadata(BUCKET_NAME,createFileNameFromID(nameSpace,key)));
        } catch (AmazonClientException e) {
            log.debug("getMetaData", e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String nameSpace, String key) {
        amazonS3.deleteObject(BUCKET_NAME,createFileNameFromID(nameSpace,key));
    }

    @Override
    public void deleteAll() {
        amazonS3.deleteObject(BUCKET_NAME,"*");
    }

    @Override
    public String getHash(String nameSpace, String key) {
        return getMetaData(nameSpace,key)
                .orElseThrow(() -> new FileStoreException("Could not find s3 Object")).getETag();
    }

    @Override public long getSize(String namespace, String key) {
        try {
            return getResource(namespace, key).contentLength();
        } catch (IOException e) {
            throw new FileStoreException(e);
        }
    }

    private Resource getResource(String namespace, String key) {
        return resourceLoader.getResource("s3://" + BUCKET_NAME + "/" + createFileNameFromID(namespace,key));
    }
}
