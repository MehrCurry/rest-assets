package de.gzockoll.prototype.assets.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class Snapshot extends AbstractEntity {

    @ManyToMany
    List<Media> included=new ArrayList<>();

    public Snapshot() {
    }

    public Snapshot(List<Media> included) {
        this.included = included;
    }
}
