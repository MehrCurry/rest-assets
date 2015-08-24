package de.gzockoll.prototype.assets.entity;

import de.gzockoll.prototype.assets.util.ValidateableObject;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class AbstractEntity extends ValidateableObject {
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @Getter
    private Date createdAt=new Date();


}
