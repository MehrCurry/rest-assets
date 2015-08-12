package de.gzockoll.prototype.assets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@WebAppConfiguration
public class AssetRepositoryApplicationTests {

	@Test
	public void contextLoads() {
	}

}
