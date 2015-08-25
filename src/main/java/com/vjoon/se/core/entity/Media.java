package com.vjoon.se.core.entity;

import com.vjoon.se.core.services.FileStore;
import com.vjoon.se.core.util.MD5Helper;
import com.vjoon.se.core.util.MediaIDGenerator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media extends AbstractEntity implements Serializable {

    private static final String PREFIX = "assets" + File.separator + "production";
    private static final Tika TIKA = new Tika();

    @Temporal(TemporalType.TIMESTAMP) private Date deletedAt;

    private String hash;

    @NotNull private String nameSpace;
    @NotNull @Size(min = 8) private String externalReference;
    @NotNull private String originalFilename;

    @NotNull private String contentType;

    private long length;

    private boolean existsInProduction = false;
    private boolean existsInArchive = false;
    @NotNull @Column(unique = true) private String mediaId;

    public void extrxactInfosFromFile(File f) {
        checkArgument(f.length() > 0);
        this.length = f.length();
        this.hash = MD5Helper.checksum(f);
        try {
            this.contentType = TIKA.detect(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getStream(FileStore fileStore) {
        checkState(fileStore.exists(nameSpace, externalReference));
        return fileStore.getStream(nameSpace, externalReference);
    }

    @PrePersist public void prePersist() {
        this.mediaId = MediaIDGenerator.generateID(nameSpace, externalReference);
    }
}
