package de.gzockoll.prototype.ams.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity @Data @EqualsAndHashCode(callSuper = false) @Slf4j public class Snapshot extends AbstractEntity {
    @Pattern(message="namespace is not alphanumeric" , regexp="^[a-zA-Z0-9]+$")
    @Size(min=3)
    private String namespace;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Asset> included = new ArrayList<>();

    public Snapshot() {
    }

    public Snapshot(String namespace,List<Asset> included) {
        this.namespace=namespace;
        this.included = included;
        included.forEach(m -> m.addSnapshot(this));
    }
}
