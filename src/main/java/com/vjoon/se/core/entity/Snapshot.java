package com.vjoon.se.core.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity @Data @EqualsAndHashCode(callSuper = false) @Slf4j public class Snapshot extends AbstractEntity {
    private NameSpace namespace;

    @ManyToMany(mappedBy = "snapshots")
    @JsonManagedReference
    List<Asset> included = new ArrayList<>();

    public Snapshot() {
    }

    public Snapshot(NameSpace namespace,List<Asset> included) {
        this.namespace=namespace;
        this.included = included;
        included.forEach(m -> m.addSnapshot(this));
    }
}
