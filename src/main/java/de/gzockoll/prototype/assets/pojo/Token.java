package de.gzockoll.prototype.assets.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Token implements Serializable {
    private final String id=UUID.randomUUID().toString();
    private final Object payload;
}
