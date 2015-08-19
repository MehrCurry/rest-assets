package de.gzockoll.prototype.assets.pojo;

import de.gzockoll.prototype.assets.entity.Media;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Token implements Serializable {
    private final String id;
    private final Media payload;

    public Token(Media payload) {
        this.id= UUID.randomUUID().toString();
        this.payload=payload;
    }
}
