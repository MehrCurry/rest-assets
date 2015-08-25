package com.vjoon.se.core.entity;

import com.vjoon.se.core.AssetRepositoryApplication;
import com.vjoon.se.core.repository.MediaRepository;
import org.assertj.core.api.StrictAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration @IntegrationTest("server.port:0") @ActiveProfiles("test") @Category(IntegrationTest.class)
public class MediaTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Autowired private MediaRepository mediaRepository;

    @Before public void setUp() throws Exception {

    }

    @After public void tearDown() throws Exception {

    }

    @Test public void testValidation() {
        Media media = new Media();
        StrictAssertions.assertThat(media.isValid()).isFalse();
        StrictAssertions.assertThat(media.validationErrors().size()).isEqualTo(5);
    }

    @Test public void testValidateBeforeSave() {
        Media media = new Media();
        thrown.expect(ConstraintViolationException.class);
        mediaRepository.save(media);
    }
}
