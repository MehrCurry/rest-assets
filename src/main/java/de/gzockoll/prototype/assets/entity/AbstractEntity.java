package de.gzockoll.prototype.assets.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class AbstractEntity {
    @Id
    @GeneratedValue
    @Getter
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @Getter
    private Date createdAt=new Date();


}
