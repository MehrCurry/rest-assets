package de.gzockoll.prototype.assets.services;

import java.io.IOException;

public class FileStoreException extends RuntimeException {
    public FileStoreException(IOException e) {
        super(e);
    }
}
