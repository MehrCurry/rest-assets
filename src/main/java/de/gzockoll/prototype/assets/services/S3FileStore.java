package de.gzockoll.prototype.assets.services;

import com.google.common.collect.ImmutableMap;
import com.hazelcast.util.Base64;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class S3FileStore implements FileStore {
    @EndpointInject(uri="direct:s3Upload")
    private ProducerTemplate template;


    @Override
    public void save(String namespace, String key, InputStream stream, boolean overwrite) {
    }

    @Override
    public InputStream getStream(String namespace, String key) {
        return null;
    }

    @Override
    public boolean exists(String nameSpace, String key) {
        return false;
    }

    @Override
    public void delete(String nameSpace, String key) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public String getHash(String nameSpace, String key) {
        return null;
    }
}
