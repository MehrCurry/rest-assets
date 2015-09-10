package com.vjoon.se.core.services;

import com.vjoon.se.core.entity.NameSpace;

import java.io.InputStream;
import java.util.Optional;

/**
 * Created by guido on 22.08.15.
 */
public interface FileStore {

    String createFileNameFromID(NameSpace nameSpace, String key);

    String createFullNameFromID(NameSpace nameSpace, String key);

    void delete(NameSpace nameSpace, String key);

    void deleteAll();

    boolean exists(NameSpace nameSpace, String key);

    String getHash(NameSpace nameSpace, String key);

    long getSize(NameSpace namespace, String key);

    InputStream getStream(NameSpace namespace, String key);

    void save(NameSpace namespace, String key, InputStream stream, Optional<String> checksum, boolean overwrite);
}
