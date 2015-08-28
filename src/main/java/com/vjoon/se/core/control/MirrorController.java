package com.vjoon.se.core.control;

import com.google.common.eventbus.Subscribe;
import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.event.AssetCreatedEvent;
import com.vjoon.se.core.event.AssetDeletedEvent;
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
    public void mediaCreated(AssetCreatedEvent event) {
        mirror(event.getMedia());
    }

    @Subscribe
    public void mediaDeleted(AssetDeletedEvent event) {
        event.getMedia().delete(mirrorFileStore);
    }

    @Async
    private void mirror(Asset media) {
        media.copy(productionFileStore, mirrorFileStore);
    }
}
