package de.gzockoll.prototype.assets.pojo;

import lombok.Data;

import java.util.UUID;

@Data
public class Token {
    private final String id;
    private final String payload;

    public Token(String payload) {
        this.id= UUID.randomUUID().toString();
        this.payload=payload;
    }
}
