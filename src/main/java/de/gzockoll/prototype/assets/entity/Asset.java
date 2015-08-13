package de.gzockoll.prototype.assets.entity;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Tag;
import com.google.common.base.Stopwatch;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkState;


@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "data")
@Getter
@Slf4j
public class Asset {
    private static final Tika TIKA = new Tika();

    @NotNull
    private String mimeType;

    private Map<String,String> metaData= Collections.emptyMap();

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
            metaData = extractMetaData(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> extractMetaData(File input) {
        try {
            return StreamSupport.stream(ImageMetadataReader.readMetadata(input).getDirectories().spliterator(), false)
                    .flatMap(d -> d.getTags().stream())
                    .collect(Collectors.toMap(t -> "[" + t.getDirectoryName() + "] " + t.getTagName() , Tag::getDescription));
        } catch (IOException | ImageProcessingException e) {
            log.debug("Error extracting Metadata " + e.getLocalizedMessage());
            return Collections.EMPTY_MAP;
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

    public String getFilename() {
        checkState(file!=null);
        return file.getName();
    }

    public String checksum() throws IOException {
        Stopwatch sw=Stopwatch.createStarted();
        HashCode hash = Files.hash(file, Hashing.md5());
        sw.stop();
        log.debug("Hash took " + sw.toString());
        log.debug("Hash for {} is {}", file.getName(), hash);
        return hash.toString();
    }
}
