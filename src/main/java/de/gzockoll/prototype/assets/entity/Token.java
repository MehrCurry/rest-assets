package de.gzockoll.prototype.assets.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Data
public class Token {
    @Id
    private final String id;
    @Indexed(expireAfterSeconds = 60)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt=new Date();
    private final String assetId;

    public static Token createFor(String id) {
        return new Token(UUID.randomUUID().toString(),id);
    }
}
