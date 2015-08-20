package de.gzockoll.prototype.assets.services;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.gzockoll.prototype.assets.entity.AbstractEntity;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import de.gzockoll.prototype.assets.events.CopyFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class MediaService {
    @Autowired
    private MediaRepository repository;

    @Autowired
    private EventBus eventBus;

    public AbstractEntity createMediaInfo(Exchange ex) {
        Media media=Media.builder().originalFilename(ex.getIn().getHeader("CamelFileName").toString()).build();
        media.extractInfosFromFile(ex.getIn().getBody(File.class));
        repository.save(media);
        return media;
    }

    public AbstractEntity findMediaInfo(Exchange ex) {
        return repository.findByFilename((String) ex.getIn().getHeader("CamelFileName")).stream().findFirst().get();
    }

    public void finished(Exchange ex) {
        eventBus.post(CopyFinishedEvent.builder()
                .media((Media) ex.getIn().getHeader("media"))
                .build());
    }

    public List<Media> getAll() {
        return repository.findAll();
    }

    @Subscribe
    public void finished(CopyFinishedEvent event) {
            Media m=repository.findOne(event.getMedia().getId());
            repository.save(m);
    }


}
