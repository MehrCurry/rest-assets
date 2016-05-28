package de.gzockoll.prototype.ams.services;

public class FileStoreException extends RuntimeException {

    public FileStoreException(Exception e) {
        super(e);
    }

    public FileStoreException(String message) {
        super(message);
    }

    public FileStoreException(String message, Exception e) {
        super(message,e);
    }
}
