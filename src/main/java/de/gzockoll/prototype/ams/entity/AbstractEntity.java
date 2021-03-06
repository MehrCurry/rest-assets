package de.gzockoll.prototype.ams.entity;

import de.gzockoll.prototype.ams.util.ValidateableObject;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass public class AbstractEntity extends ValidateableObject {

    @Id @GeneratedValue @Getter private Long id;

    @Temporal(TemporalType.TIMESTAMP) @Column(updatable = false) @Getter private Date createdAt = new Date();

}
