package de.gzockoll.prototype.assets.entity;

import com.google.common.base.Stopwatch;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.Tika;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.io.*;

import static com.google.common.base.Preconditions.checkState;


@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "data")
@Getter
@Slf4j
public class Asset {
    private static final Tika TIKA = new Tika();

    @NotNull
    private String mimeType;


    @Transient
    private File file;

    public Asset() {
    }

    public Asset(File input) {
        try {
            this.file=input;
            Stopwatch t=Stopwatch.createStarted();
            this.mimeType = TIKA.detect(getAsStream());
            t.stop();
            log.debug("Took " + t.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getAsStream() {
        checkState(file!=null);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public long getSize() {
        return file.length();
    }

    public double sizeInKB() {
        return getSize()/1024.0;
    }

    public String checksum() {
        try {
            return DigestUtils.md5Hex(getAsStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getFilename() {
        checkState(file!=null);
        return file.getName();
    }
}
