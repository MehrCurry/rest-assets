package de.gzockoll.prototype.assets.control;

import de.gzockoll.prototype.assets.AssetRepositoryApplication;
import de.gzockoll.prototype.assets.categories.SlowTest;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.pojo.Token;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
@Category(IntegrationTest.class)
public class TokenControllerTest {
    @Autowired
    private TokenController controller;

    @Autowired
    private MediaRepository repository;
    private Media media;

    @Before
    public void setUp() {
        media = new Media();
        repository.save(media);
    }

    @After
    public void teadDown() {
        repository.delete(media);
    }

    @Test
    @Category(SlowTest.class)
    public void testExpireToken() throws Exception {
        Token t=controller.createToken(media.getMediaId());
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
        Thread.sleep(65000);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }
}