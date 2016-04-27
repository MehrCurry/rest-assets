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
import java.io.IOException;
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
public class Asset extends AbstractEntity implements Serializable {

    private static final String PREFIX = "assets" + File.separator + "production";
    private static final Tika TIKA = new Tika();

    @Temporal(TemporalType.TIMESTAMP) private Date deletedAt;

    private String hash;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Snapshot> snapshots;

    @NotNull
    @Embedded
    private NameSpace nameSpace;
    @NotNull @Size(min = 8) @Column(name = "reference") private String key;
    @NotNull private String originalFilename;

    @NotNull private String contentType;

    private long length;

    private boolean existsInProduction = false;

    @NotNull @Column(unique = true) private String mediaId;

    @JsonIgnore
    public InputStream getStream(FileStore fileStore) {
        checkState(fileStore.exists(nameSpace, key));
        return fileStore.getStream(nameSpace, key);
    }

    @PrePersist public void prePersist() {
        this.mediaId = MediaIDGenerator.generateID(nameSpace, key);
    }

    public void addSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    public void remove(Snapshot s) {
        checkState(snapshots.contains(s));
        snapshots.remove(s);
    }

    public void copy(@NotNull FileStore from, @NotNull FileStore to) {
        try (InputStream stream = getStream(from)) {
            to.save(nameSpace, key, stream, Optional.of(hash), true);
        } catch (IOException e) {
            log.warn("Problem with stream",e );
        }
    }

    public void delete(FileStore fileStore) {
        fileStore.delete(nameSpace, key);
    }
}
