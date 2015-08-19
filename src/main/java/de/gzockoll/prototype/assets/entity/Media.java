package de.gzockoll.prototype.assets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.gzockoll.prototype.assets.util.MD5Helper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import javax.persistence.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@JsonIgnoreProperties("inputStream")
public class Media extends AbstractEntity {
    private static final Tika TIKA = new Tika();

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @Column(unique=true)
    private String mediaId=UUID.randomUUID().toString();
    private String hash;
    private String filename=generateFullname();
    private String originalFilename;
    private String contentType;
    private long length;
    @Enumerated(EnumType.STRING)
    private MirrorSystem system= MirrorSystem.LOCAL_FILE;

    private boolean existsInProduction=false;
    private boolean existsInArchive=false;

    public Media() {
    }

    public Media(String filename) {
        this.originalFilename=filename;
    }

    public String generateFilename() {
        return mediaId.replace("-", "");
    }

    public String generatePath(String name) {
        String parts[] = name.substring(0,9).split("(?<=\\G.{3})");
        return Arrays.stream(parts).collect(Collectors.joining(File.separator));
    }

    public String generateFullname() {
        String name=generateFilename();
        return generatePath(name) + File.separator + name;
    }

    @Transient
    public InputStream getInputStream() {
        return system.getStream(this);
    }

    public void extractInfosFromFile(File f) {
        checkArgument(f.length()>0);
        this.length=f.length();
        this.hash= MD5Helper.checksum(f);
        try {
            this.contentType = TIKA.detect(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFromProduction() {
        checkState(existsInProduction);
        String name="assets" + File.separator + "production" + File.separator + filename;
        deletePath(name);
        existsInProduction=false;
    }

    public void deleteFromArchive() {
        checkState(existsInArchive);
        String name="assets" + File.separator + "archive" + File.separator + filename;
        deletePath(name);
        existsInArchive=false;
    }

    public void deleteFiles() {
        if (existsInProduction)
            deleteFromProduction();
        if (existsInArchive)
            deleteFromArchive();
    }

    public void deletePath(String name) {
        Path directory = Paths.get(name);
        try {
            Files.deleteIfExists(directory);
            while ((directory=directory.getParent())!=null) {
                    File f=directory.toFile();
                    if (f.isDirectory() && f.exists() && f.list().length==0)
                        Files.delete(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
