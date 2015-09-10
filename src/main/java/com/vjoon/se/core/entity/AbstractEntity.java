package com.vjoon.se.core.entity;

import com.vjoon.se.core.util.ValidateableObject;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass public class AbstractEntity extends ValidateableObject {

    @Id @GeneratedValue @Getter private Long id;

    @Temporal(TemporalType.TIMESTAMP) @Column(updatable = false) @Getter private Date createdAt = new Date();

    @Version
    private long version;

}
