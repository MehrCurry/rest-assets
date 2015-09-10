package com.vjoon.se.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vjoon.se.core.util.ValidateableObject;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass public class AbstractEntity extends ValidateableObject {

    @Id @GeneratedValue @Getter private Long id;

    @Temporal(TemporalType.TIMESTAMP) @Column(updatable = false) @Getter private Date createdAt = new Date();

    @Override
    @Transient
    @JsonIgnore
    public boolean isValid() {
        return super.isValid();
    }
}
