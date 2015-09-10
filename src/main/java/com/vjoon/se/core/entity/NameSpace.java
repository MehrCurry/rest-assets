package com.vjoon.se.core.entity;

import com.vjoon.se.core.util.ValidateableObject;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Embeddable
public class NameSpace extends ValidateableObject implements Serializable {
    @Pattern(message="namespace is not alphanumeric" , regexp="^[a-zA-Z0-9]+$")
    @Size(min=3)
    private final String namespace;

    private NameSpace() {this.namespace=null;}

    public NameSpace(String aString) {
        this.namespace=aString;
    }
}
