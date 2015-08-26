package com.vjoon.se.core.control;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.event.MediaCreatedEvent;
import com.vjoon.se.core.services.FileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class S3Controller {
    @Autowired
    private FileStore fileStore;

    @EndpointInject
    private ProducerTemplate producerTemplate;


    @Subscribe
    public void mediaCreated(MediaCreatedEvent event) {
        sendTo("direct:s3tmp", event.getMedia());
    }

    @Async
    public void sendTo(String target,Media media) {
        Map<String,Object> headers= ImmutableMap.of(
                "CamelFileName", fileStore.createFileNameFromID(media.getNameSpace(), media.getExternalReference()),
                "Checksum", media.getHash(),
                "CamelAwsS3Headers", ImmutableMap.of("originalFilename", media.getOriginalFilename())
        );
        producerTemplate.sendBodyAndHeaders(target, fileStore.getStream(media.getNameSpace(), media.getExternalReference()), headers);
    }
}
