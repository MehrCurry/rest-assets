package com.vjoon.se.core.util;

import com.google.common.collect.ImmutableList;
import com.vjoon.se.core.entity.NameSpace;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaIDGeneratorTest {

    @Test
    public void testIdGenerator() {
        String id1=MediaIDGenerator.generateID(new NameSpace("1"),"key");
        String id2=MediaIDGenerator.generateID(new NameSpace("2"), "key");
        String id3=MediaIDGenerator.generateID(null,"key");
        String id4=MediaIDGenerator.generateID(null,"bla");

        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1).isNotEqualTo(id3);
        assertThat(id1).isNotEqualTo(id4);

        assertThat(id2).isNotEqualTo(id3);
        assertThat(id2).isNotEqualTo(id4);

        assertThat(id3).isNotEqualTo(id4);
    }

    @Test
    public void testIdGeneratorEquality() {
        String id1 = MediaIDGenerator.generateID(new NameSpace("1"), "key");
        String id2 = MediaIDGenerator.generateID(new NameSpace("1"), "key");

        assertThat(id1).isEqualTo(id2);
    }

    @Test
    public void testIdGeneratorSameNameSpaceDifferentKeys() {
        String id1 = MediaIDGenerator.generateID(new NameSpace("1"), "12345678");
        String id2 = MediaIDGenerator.generateID(new NameSpace("1"), "xxx12345678");

        assertThat(id1).isNotEqualTo(id2);
    }
}
