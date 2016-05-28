package de.gzockoll.prototype.ams.camel;

import de.gzockoll.prototype.ams.util.MD5Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class ChecksumVerifier {
    private final MD5Helper md5Helper;

    @Autowired
    public ChecksumVerifier(MD5Helper md5Helper) {
        this.md5Helper = md5Helper;
    }

    public void verify(@Header("CamelFileNameProduced") String fileName, @Header("Checksum") String checksum) throws IOException {
        if (!checksum.equalsIgnoreCase(md5Helper.checksum(fileName))) {
            throw new IOException("Checksum mismatch");
        }
    }
}
