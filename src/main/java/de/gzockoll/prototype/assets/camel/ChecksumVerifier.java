package de.gzockoll.prototype.assets.camel;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.gzockoll.prototype.assets.entity.Media;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ChecksumVerifier implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String filename=exchange.getIn().getHeader("CamelFileName").toString();
        Media media= (Media) exchange.getIn().getHeader("media");
        if (media.getHash()!=checksum(filename)) {
            throw new IOException("Checksum mismatch");

        }
    }

    public String checksum(String filename) throws IOException {
        File file=new File(filename);
        HashCode hash = Files.hash(file, Hashing.md5());
        return hash.toString();
    }

}
