package com.vjoon.se.core.camel;

import com.vjoon.se.core.services.ChecksumCalculator;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class ChecksumVerifier {
    @Autowired
    @Setter(AccessLevel.PACKAGE)
    private ChecksumCalculator calc;

    public void verify(@Header("CamelFileNameProduced") String fileName, @Header("Checksum") String checksum) throws IOException {
        if (!checksum.equalsIgnoreCase(calc.checksum(fileName))) {
            throw new IOException("Checksum mismatch");
        }
    }
}
