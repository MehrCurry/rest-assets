package com.vjoon.se.core.services;

import org.apache.camel.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileWriter {
    public void writeToFile(InputStream aStream,@Header("targetName")String filename) throws IOException {
        Path target = Paths.get(filename);
        Files.createDirectories(target.getParent());
        Files.copy(aStream, target);
    }
}
