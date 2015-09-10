package com.vjoon.se.core.entity;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NameSpaceTest {

    @Test
    public void testIsValid() {
        assertThat(new NameSpace("bla").isValid()).isTrue();
        assertThat(new NameSpace("xy").isValid()).isFalse();
        assertThat(new NameSpace("bla/bla").isValid()).isFalse();
        assertThat(new NameSpace("bla.bla.bla").isValid()).isFalse();
        assertThat(new NameSpace("b1A").isValid()).isTrue();
    }
}
