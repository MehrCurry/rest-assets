package com.vjoon.se.core.services;

import java.io.InputStream;

/**
 * Created by guido on 22.08.15.
 */
public interface FileStore {

    void save(String namespace, String key, InputStream stream, boolean overwrite);

    String createFileNameFromID(String nameSpace, String key);

    String createFullNameFromID(String nameSpace, String key);

    InputStream getStream(String namespace, String key);

    boolean exists(String nameSpace, String key);

    void delete(String nameSpace, String key);

    void deleteAll();

    String getHash(String nameSpace, String key);
}
