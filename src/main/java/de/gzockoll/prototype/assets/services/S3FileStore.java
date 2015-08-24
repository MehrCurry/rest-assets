package de.gzockoll.prototype.assets.services;

import java.io.InputStream;

public class S3FileStore implements FileStore {
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
