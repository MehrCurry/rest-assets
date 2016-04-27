package com.vjoon.se.core.services;

import com.google.common.base.Stopwatch;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.hazelcast.util.Base64;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class ChecksumCalculator {

    @Getter
    private HashFunction hashFunction;

    @Autowired
    public ChecksumCalculator(HashFunction hashFunction) {
        this.hashFunction=hashFunction;
    }

    public String checksum(File file) {
        return new String(calculateHash(file));
    }

    public String checksum(String filename) {
        return checksum(new File(filename));
    }

    public byte[] calculateHash(File file) {
        return calculateHash(file,hashFunction);
    }

    public byte[] calculateHash(File file,HashFunction function) {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            return Base64.encode(Files.hash(file, function).asBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            log.debug("Hash ({}) took {}",hashFunction.toString(), sw.toString());
        }
    }
    public byte[] calculateMD5Hash(File file) {
        return calculateHash(file, Hashing.md5());
    }

}
