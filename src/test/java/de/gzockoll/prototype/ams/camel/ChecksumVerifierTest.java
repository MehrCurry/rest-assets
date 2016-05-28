package de.gzockoll.prototype.ams.camel;

import de.gzockoll.prototype.ams.util.MD5Helper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChecksumVerifierTest {

    private ChecksumVerifier verifier;
    private Path testPath;

    @Rule
    public ExpectedException thrown=ExpectedException.none();
    private MD5Helper helper;

    @Before
    public void setUp() throws Exception {
        helper = mock(MD5Helper.class);
        this.verifier=new ChecksumVerifier(helper);
        this.testPath=Files.createTempFile("junit-",".txt");
        Files.write(testPath, "JUnit Test".getBytes());
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(testPath);
    }

    @Test
    public void testVerify() throws Exception {
        when(helper.checksum(anyString())).thenReturn("123");
        verifier.verify(testPath.normalize().toString(), "123");
    }

    @Test
    public void testVerifyFails() throws Exception {
        thrown.expect(IOException.class);
        verifier.verify(testPath.normalize().toString(),"junit");
    }
}
