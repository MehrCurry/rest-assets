package de.gzockoll.prototype.assets.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@Slf4j
public class Media {
    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt=new Date();
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @Column(unique=true)
    private String mediaId=UUID.randomUUID().toString();
    private String hash;
    private String filename=generateFullname();
    private String originalFilename;

    @Transient
    private Map<String,String> locations=new HashMap<>();

    public Media() {
    }

    public Media(String filename) {
        this.originalFilename=filename;
    }

    public void addLocation(String key, String value) {
        locations.put(key, value);
    }

    public String generateFilename() {
        return mediaId.replace("-", "");
    }

    public String generatePath(String name) {
        String parts[] = name.split("(?<=\\G.{9})");
        return Arrays.stream(parts).collect(Collectors.joining(File.separator));
    }

    public String generateFullname() {
        String name=generateFilename();
        return generatePath(name) + File.separator + name;
    }
}
