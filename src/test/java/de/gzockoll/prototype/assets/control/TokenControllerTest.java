package de.gzockoll.prototype.assets.control;

import com.hazelcast.core.IMap;
import de.gzockoll.prototype.assets.AssetRepositoryApplication;
import de.gzockoll.prototype.assets.pojo.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
public class TokenControllerTest {
    @Autowired
    private TokenController controller;

    @Test
    public void testCreateToken() throws Exception {
        Token t=controller.createToken("JUnit");
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
        Thread.sleep(8000);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }

    @Test
    public void testResolve() throws Exception {

    }
}