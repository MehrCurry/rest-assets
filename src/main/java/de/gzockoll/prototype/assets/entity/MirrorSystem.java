package de.gzockoll.prototype.assets.entity;

import de.gzockoll.prototype.assets.boundary.storage.FileSystemAdapter;
import de.gzockoll.prototype.assets.boundary.storage.GridFSAdapter;
import de.gzockoll.prototype.assets.boundary.storage.S3Adapter;
import lombok.Getter;

import java.io.InputStream;

public enum MirrorSystem {
    LOCAL_FILE(new FileSystemAdapter()),S3(new S3Adapter()),GRIDFS(new GridFSAdapter());

    @Getter
    private final StorageAdapter adapter;

    MirrorSystem(StorageAdapter adapter) {
        this.adapter = adapter;
    }

    public InputStream getStream(Media media) {
        return adapter.getStream(media);
    }
}
