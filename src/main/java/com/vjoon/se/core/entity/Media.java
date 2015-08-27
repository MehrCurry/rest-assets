package com.vjoon.se.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vjoon.se.core.services.FileStore;
import com.vjoon.se.core.util.MediaIDGenerator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

@Entity
@Data
@EqualsAndHashCode(callSuper = false,exclude = "snapshots")
@ToString(exclude = "snapshots")
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media extends AbstractEntity implements Serializable {

    private static final String PREFIX = "assets" + File.separator + "production";
    private static final Tika TIKA = new Tika();

    @Temporal(TemporalType.TIMESTAMP) private Date deletedAt;

    private String hash;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Snapshot> snapshots;

    @NotNull private String nameSpace;
    @NotNull @Size(min = 8) private String externalReference;
    @NotNull private String originalFilename;

    @NotNull private String contentType;

    private long length;

    private boolean existsInProduction = false;

    @NotNull @Column(unique = true) private String mediaId;

    @JsonIgnore
    public InputStream getStream(FileStore fileStore) {
        checkState(fileStore.exists(nameSpace, externalReference));
        return fileStore.getStream(nameSpace, externalReference);
    }

    @PrePersist public void prePersist() {
        this.mediaId = MediaIDGenerator.generateID(nameSpace, externalReference);
    }

    public void addSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    public void remove(Snapshot s) {
        checkState(snapshots.contains(s));
        snapshots.remove(s);
    }

    public void copy(@NotNull FileStore from, @NotNull FileStore to) {
        to.save(nameSpace,externalReference,getStream(from), Optional.of(hash),false);
    }
}
