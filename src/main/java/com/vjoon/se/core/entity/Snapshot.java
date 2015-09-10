package com.vjoon.se.core.entity;

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
    private NameSpace namespace;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
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
