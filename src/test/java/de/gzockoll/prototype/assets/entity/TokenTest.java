package de.gzockoll.prototype.assets.entity;

import de.gzockoll.prototype.assets.AssetRepositoryApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@Slf4j
public class TokenTest {

    @Test
    public void testCreateToken() {
        Token token = Token.createFor("hurz");
        assertThat(token).isNotNull();
    }
}