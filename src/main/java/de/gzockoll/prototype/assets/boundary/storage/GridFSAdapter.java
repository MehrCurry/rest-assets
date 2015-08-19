package de.gzockoll.prototype.assets.boundary.storage;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.entity.StorageAdapter;

import java.io.InputStream;

/**
 * Created by guido on 19.08.15.
 */
public class GridFSAdapter implements StorageAdapter {
    @Override
    public InputStream getStream(Media media) {
        throw new UnsupportedOperationException();
    }
}
