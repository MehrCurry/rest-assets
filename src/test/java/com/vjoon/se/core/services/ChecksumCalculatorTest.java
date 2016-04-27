package com.vjoon.se.core.services;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.hazelcast.util.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by guido on 23.09.15.
 */
public class ChecksumCalculatorTest {
    private static final HashFunction HASH_FUNCTION = Hashing.crc32();
    private ChecksumCalculator calc;
    private Path path;

    @Before
    public void setUp() throws IOException {
        path = Files.createTempFile("bla", "bla");
        Files.write(path,"sdfsdfsdfsdfsdfsdfsdfsdf".getBytes());
        calc=new ChecksumCalculator(HASH_FUNCTION);
    }

    @After
    public void tearDown() throws IOException {
        Files.delete(path);
    }

    @Test
    public void testChecksum() {
        String hash = calc.checksum(path.toFile());
        assertThat(hash).doesNotContain("@");
    }

    @Test
    public void testStreaming() throws IOException {
        OutputStream output = mock(OutputStream.class);
        try (HashingInputStream stream = new HashingInputStream(calc.getHashFunction(), new FileInputStream(path.toFile()))) {
            IOUtils.copy(stream, output);

            byte[] hash1 = encode(stream.hash().asBytes());
            byte[] hash2 = calc.calculateHash(path.toFile());

            assertThat(hash1).isEqualTo(hash2);
        }

    }



    private byte[] encode(byte[] bytes) {
        return Base64.encode(bytes);
    }
}