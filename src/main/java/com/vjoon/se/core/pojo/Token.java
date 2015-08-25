package com.vjoon.se.core.pojo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data public class Token implements Serializable {

    @NotNull private final String id = UUID.randomUUID().toString();
    @NotNull private final Object payload;
}
