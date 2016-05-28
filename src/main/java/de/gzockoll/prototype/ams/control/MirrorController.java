package de.gzockoll.prototype.ams.control;

import com.google.common.eventbus.Subscribe;
import de.gzockoll.prototype.ams.entity.Asset;
import de.gzockoll.prototype.ams.event.AssetCreatedEvent;
import de.gzockoll.prototype.ams.event.AssetDeletedEvent;
import de.gzockoll.prototype.ams.services.FileStore;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
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

    private void mirror(Asset media) {
        media.copy(productionFileStore, mirrorFileStore);
    }
}
