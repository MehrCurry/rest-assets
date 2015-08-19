package de.gzockoll.prototype.assets.control;

import com.hazelcast.core.IMap;
import de.gzockoll.prototype.assets.AssetRepositoryApplication;
import de.gzockoll.prototype.assets.categories.SlowTest;
import de.gzockoll.prototype.assets.pojo.Token;
import org.junit.Test;
import org.junit.experimental.categories.Category;
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
    @Category(SlowTest.class)
    public void testExpireToken() throws Exception {
        Token t=controller.createToken("JUnit");
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isTrue();
        Thread.sleep(65000);
        assertThat(controller.getTokenFor(t.getId()).isPresent()).isFalse();
    }

    @Test
    public void testResolve() throws Exception {
        Token t1=controller.createToken("JUnit");
        Token t2=controller.createToken("Test");
        assertThat(controller.resolve(t1.getId()).get()).isEqualTo("JUnit");
        assertThat(controller.resolve(t2.getId()).get()).isEqualTo("Test");

    }
}