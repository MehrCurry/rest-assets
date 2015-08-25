package com.vjoon.se.core.camel;

import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.util.MD5Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

import java.io.IOException;

@Slf4j public class ChecksumVerifier implements org.apache.camel.Processor {

    @Override public void process(Exchange exchange) throws Exception {
        String filename = exchange.getIn().getHeader("CamelFileNameProduced").toString();
        Media media = (Media) exchange.getIn().getHeader("media");
        if (!media.getHash().equals(MD5Helper.checksum(filename))) {
            throw new IOException("Checksum mismatch");

        }
    }
}