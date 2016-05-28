package de.gzockoll.prototype.ams.util;

import com.amazonaws.util.Base64;
import com.google.common.base.Stopwatch;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Component
public class MD5Helper {
    @Autowired
    private HashFunction hashFunction;

    public String checksum(File file) {
        Stopwatch sw = Stopwatch.createStarted();
        HashCode hash = null;
        try {
            hash = Files.hash(file, hashFunction);
            sw.stop();
            log.debug("Hash took " + sw.toString());
            log.debug("Hash for {} is {}", file.getName(), hash);
            return hash.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(file.toString());
        }
    }

    public String checksum(String filename) {
        return checksum(new File(filename));
    }

    public byte[] calculateS3Hash(File file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return Base64.encode(DigestUtils.md5(stream));
        }
    }

}
