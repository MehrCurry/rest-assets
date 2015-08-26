package com.vjoon.se.core.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity @Data @EqualsAndHashCode(callSuper = false) @Slf4j public class Snapshot extends AbstractEntity {

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Media> included = new ArrayList<>();

    public Snapshot() {
    }

    public Snapshot(List<Media> included) {
        this.included = included;
        included.forEach(m -> m.addSnapshot(this));
    }
}
