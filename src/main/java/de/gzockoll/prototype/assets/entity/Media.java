package de.gzockoll.prototype.assets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.gzockoll.prototype.assets.services.FileStore;
import de.gzockoll.prototype.assets.util.MD5Helper;
import de.gzockoll.prototype.assets.util.MediaIDGenerator;
import de.gzockoll.prototype.assets.util.ValidateableObject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.Tika;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.*;

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

    @NotNull
    private String nameSpace;
    @NotNull
    @Size(min=8)
    private String externalReference;
    @NotNull
    private String originalFilename;

    @NotNull
    private String contentType;

    private long length;

    private boolean existsInProduction=false;
    private boolean existsInArchive=false;
    @NotNull
    @Column(unique = true)
    private String mediaId;

    public void extrxactInfosFromFile(File f) {
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

    @PrePersist
    public void prePersist() {
        this.mediaId= MediaIDGenerator.generateID(nameSpace,externalReference);
    }
}
