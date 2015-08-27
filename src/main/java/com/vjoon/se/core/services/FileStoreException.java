package com.vjoon.se.core.services;

import java.io.IOException;

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
