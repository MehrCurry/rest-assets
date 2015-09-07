package com.vjoon.se.core.services;

public class DuplicateKeyException extends FileStoreException {
    public DuplicateKeyException(Exception e) {
        super(e);
    }

    public DuplicateKeyException(String message) {
        super(message);
    }

    public DuplicateKeyException(String message, Exception e) {
        super(message, e);
    }
}
