package de.gzockoll.prototype.ams.services;

import java.io.InputStream;
import java.util.Optional;

/**
 * Created by guido on 22.08.15.
 */
public interface FileStore {

    void save(String namespace, String key, InputStream stream, Optional<String> checksum, boolean overwrite);

    String createFileNameFromID(String nameSpace, String key);

    String createFullNameFromID(String nameSpace, String key);

    InputStream getStream(String namespace, String key);

    boolean exists(String nameSpace, String key);

    void delete(String nameSpace, String key);

    void deleteAll();

    String getHash(String nameSpace, String key);

    long getSize(String namespace, String key);
}
