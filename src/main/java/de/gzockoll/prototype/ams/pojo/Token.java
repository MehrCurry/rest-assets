package de.gzockoll.prototype.ams.pojo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data public class Token implements Serializable {

    @NotNull private final String id = UUID.randomUUID().toString();
    private final LocalDateTime created=LocalDateTime.now();
    @NotNull private final Object payload;
    private final TokenType tokenType;
}
