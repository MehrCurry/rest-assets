package com.vjoon.se.core.camel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChecksumVerifierTest {

    private ChecksumVerifier verifier;
    private Path testPath;

    @Rule
    public ExpectedException thrown=ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.verifier=new ChecksumVerifier();
        this.testPath=Files.createTempFile("junit-",".txt");
        Files.write(testPath, "JUnit Test".getBytes());
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(testPath);
    }

    @Test
    public void testVerify() throws Exception {
        verifier.verify(testPath.normalize().toString(), "41047c81be99e7e4e9124f35bfa19b0c");
    }

    @Test
    public void testVerifyFails() throws Exception {
        thrown.expect(IOException.class);
        verifier.verify(testPath.normalize().toString(),"junit");
    }
}
