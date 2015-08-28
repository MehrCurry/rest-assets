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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class S3Controller {
    @Autowired
    @Qualifier("production")
    private FileStore productionFileStore;

    @Autowired
    @Qualifier("s3")
    private FileStore s3FileStore;

    @EndpointInject
    private ProducerTemplate producerTemplate;


    @Subscribe
    public void mediaCreated(MediaCreatedEvent event) {
        sendToS3(event.getMedia());
    }

    @Subscribe
    public void mediaDeleted(MediaDeletedEvent event) {
        event.getMedia().delete(s3FileStore);
    }

    @Async
    public void sendToS3(Media media) {
        media.copy(productionFileStore, s3FileStore);
    }
}
