package de.gzockoll.prototype.assets.entity;

import com.google.common.base.Stopwatch;
import de.gzockoll.prototype.assets.AssetRepositoryApplication;
import de.gzockoll.prototype.assets.services.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@Slf4j
public class MediaTest {
    @Autowired
    private TestService service;

    @Autowired
    private MediaRepository repository;

    @After
    @Before
    public void cleanUp() {
        repository.deleteAll();
    }

    @Test
    public void testHighVolume() {
        IntStream.range(0, 100).forEach(i -> service.massInsert(10000));
        Optional<Media> sample=repository.findByFilename("file#99").stream().findFirst();
        if (sample.isPresent()) {
            Stopwatch sw = Stopwatch.createStarted();
            List<Media> results = repository.findByMediaId(sample.get().getMediaId());
            assertThat(results).hasSize(1);
            sw.stop();
            log.debug("Search took: " + sw.toString());
        }
    }

}