package com.vjoon.se.core.services;

import java.io.IOException;

public class FileStoreException extends RuntimeException {

    public FileStoreException(IOException e) {
        super(e);
    }
}
