package de.gzockoll.prototype.assets.entity;

import com.google.common.io.ByteStreams;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;


@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "data")
@Getter
public class Asset {
    private static final Tika TIKA = new Tika();

    @NotNull
    private String mimeType;
    @NotNull
    private byte[] data;

    @NotNull
    private String filename;

    public Asset() {
    }

    public Asset(byte[] data, String filename) {
        this(new ByteArrayInputStream(data),filename);
    }

    public Asset(InputStream is, String filename) {
        try {
            this.data = ByteStreams.toByteArray(is);
            this.mimeType = TIKA.detect(data);
            this.filename = filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Asset(String path) {
        final Path aPath = Paths.get(path);
        createFromPath(aPath);
    }

    private void createFromPath(Path aPath) {
        try {
            this.data = Files.readAllBytes(aPath);
            this.mimeType = TIKA.detect(data);
            this.filename = aPath.getFileName().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Asset(File input) {
        createFromPath(input.toPath());
    }

    public long getSize() {
        return data != null ? data.length : 0L;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public InputStream asByteStream() {
        return new ByteArrayInputStream(data);
    }

    public long sizeInKB() {
        return getSize()/1024L;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public static byte[] calculateHash(byte[] data) {
        try {
            MessageDigest calc = MessageDigest.getInstance("MD5");
            return calc.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String checksum() {
        return checksum(data);
    }

    public static String checksum(byte[] data) {
        return Arrays.asList(ArrayUtils.toObject(calculateHash(data))).stream().map(b -> String.format("%02X",0xff & b).toLowerCase()).collect(Collectors.joining());
    }
}
