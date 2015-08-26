package de.gzockoll.prototype.assets.control;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.event.MediaCreatedEvent;
import de.gzockoll.prototype.assets.services.FileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MirrorController {
    @Autowired
    private FileStore fileStore;

    @EndpointInject
    private ProducerTemplate producerTemplate;


    @Subscribe
    public void mediaCreated(MediaCreatedEvent event) {
        sendTo("direct:mirror",event.getMedia());
    }

    @Async
    public void sendTo(String target,Media media) {
        Map<String,Object> headers= ImmutableMap.of(
                "CamelFileName", fileStore.createFileNameFromID(media.getNameSpace(), media.getExternalReference()),
                "Checksum", media.getHash()
        );
        producerTemplate.sendBodyAndHeaders(target, fileStore.getStream(media.getNameSpace(), media.getExternalReference()), headers);
    }
}
