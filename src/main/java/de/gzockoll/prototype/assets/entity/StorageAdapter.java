package de.gzockoll.prototype.assets.entity;

import java.io.InputStream;

/**
 * Created by guido on 19.08.15.
 */
public interface StorageAdapter {
    InputStream getStream(Media media);
}
