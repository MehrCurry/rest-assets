package de.gzockoll.prototype.assets.services;

import com.google.common.base.Stopwatch;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.stream.IntStream;

@Transactional
@Component
@Slf4j
public class TestService {
    @Autowired
    private MediaRepository repository;
    public void massInsert(int count) {
        Stopwatch sw=Stopwatch.createStarted();
        IntStream.range(0, count).forEach(i-> {
                    Media m=new Media();
                    m.setFilename("file#" + i);
                    repository.save(m);
                }
        );
        sw.stop();
        log.debug("Insert took " + sw.toString());
    }
}
