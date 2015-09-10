package com.vjoon.se.core.entity;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Person extends AbstractEntity {
    private String firstName;
}
