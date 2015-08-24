package de.gzockoll.prototype.assets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.gzockoll.prototype.assets.services.FileStore;
import de.gzockoll.prototype.assets.util.MD5Helper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@JsonIgnoreProperties("inputStream")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media extends AbstractEntity implements Serializable {
    private static final String PREFIX="assets" + File.separator + "production";
    private static final Tika TIKA = new Tika();

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    private String hash;

    private String nameSpace;
    private String externalReference;
    private String originalFilename;

    private String contentType;

    private long length;

    private boolean existsInProduction=false;
    private boolean existsInArchive=false;
    private String mediaId;

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

    public InputStream getStream(FileStore fileStore) {
        checkState(fileStore.exists(nameSpace, externalReference));
        return fileStore.getStream(nameSpace,externalReference);
    }

    public String createMediaId() {
        return nameSpace + File.separator + externalReference;
    }

    @PrePersist
    public void prePersist() {
        this.mediaId=createMediaId();
    }
}
