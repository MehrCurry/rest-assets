package com.vjoon.se.core.util;

import com.amazonaws.util.Base64;
import com.google.common.base.Stopwatch;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j public class MD5Helper {

    public static String checksum(File file) {
        Stopwatch sw = Stopwatch.createStarted();
        HashCode hash = null;
        try {
            hash = Files.hash(file, Hashing.md5());
            sw.stop();
            log.debug("Hash took " + sw.toString());
            log.debug("Hash for {} is {}", file.getName(), hash);
            return hash.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(file.toString());
        }
    }

    public static String checksum(String filename) {
        return checksum(new File(filename));
    }

    public byte[] calculateS3Hash(File file) throws IOException {
        return Base64.encode(DigestUtils.md5(new FileInputStream(file)));
    }

}
