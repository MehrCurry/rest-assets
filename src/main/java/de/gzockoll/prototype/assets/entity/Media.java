package de.gzockoll.prototype.assets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.gzockoll.prototype.assets.util.MD5Helper;
import lombok.*;
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
@NoArgsConstructor
@AllArgsConstructor
public class Media extends AbstractEntity implements Serializable {
    private static final String PREFIX="assets" + File.separator + "production";
    private static final Tika TIKA = new Tika();

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @Column(unique=true)
    private String mediaId=UUID.randomUUID().toString();
    private String hash;

    @Column(unique=true)
    private String filename=generateFullname();
    private String nameSpace;

    private String externalReference;
    private String originalFilename;

    private String contentType;

    private long length;

    private boolean existsInProduction=false;
    private boolean existsInArchive=false;

    public Media(String originalFilename, String contentType, long length) {
        this.originalFilename=originalFilename;
        this.contentType=contentType;
        this.length=length;
    }

    @Builder
    public static Media create(String originalFilename,String contentType,long length) {
        return new Media(originalFilename,contentType,length);
    };


    public String generateFilename() {
        return mediaId.replace("-", "");
    }

    public String generatePath(String name) {
        String parts[] = name.substring(0,8).split("(?<=\\G.{2})");
        return Arrays.stream(parts).collect(Collectors.joining(File.separator));
    }

    public String generateFullname() {
        String name=generateFilename();
        return PREFIX + File.separator + (nameSpace!=null ? nameSpace : "public") + File.separator +  generatePath(name) + File.separator + name;
    }

    @Transient
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(filename);
    }

    public void extractInfosFromFile(File f) {
        checkArgument(f.length() > 0);
        this.length=f.length();
        this.hash= MD5Helper.checksum(f);
        try {
            this.contentType = TIKA.detect(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFromProduction() {
        deletePath(filename);
        existsInProduction=false;
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
