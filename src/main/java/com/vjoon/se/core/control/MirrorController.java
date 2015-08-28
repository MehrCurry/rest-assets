package com.vjoon.se.core.control;

import com.google.common.eventbus.Subscribe;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.event.MediaCreatedEvent;
import com.vjoon.se.core.event.MediaDeletedEvent;
import com.vjoon.se.core.services.FileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("localMirror")
public class MirrorController {
    @Autowired
    @Qualifier("production")
    private FileStore productionFileStore;

    @Autowired
    @Qualifier("mirror")
    private FileStore mirrorFileStore;

    @EndpointInject
    private ProducerTemplate producerTemplate;


    @Subscribe
    public void mediaCreated(MediaCreatedEvent event) {
        mirror(event.getMedia());
    }

    @Subscribe
    public void mediaDeleted(MediaDeletedEvent event) {
        event.getMedia().delete(mirrorFileStore);
    }

    @Async
    private void mirror(Media media) {
        media.copy(productionFileStore, mirrorFileStore);
    }
}
