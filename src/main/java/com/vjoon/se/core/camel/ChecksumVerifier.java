package com.vjoon.se.core.camel;

import de.gzockoll.prototype.assets.util.MD5Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class ChecksumVerifier {
    public void verify(@Header("CamelFileNameProduced") String fileName, @Header("Checksum") String checksum) throws IOException {
        if (!checksum.equalsIgnoreCase(MD5Helper.checksum(fileName))) {
            throw new IOException("Checksum mismatch");
        }
    }
}
