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
@Profile("s3Mirror")
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
    public void mediaCreated(AssetCreatedEvent event) {
        sendToS3(event.getMedia());
    }

    @Subscribe
    public void mediaDeleted(AssetDeletedEvent event) {
        event.getMedia().delete(s3FileStore);
    }

    public void sendToS3(Asset media) {
        media.copy(productionFileStore, s3FileStore);
    }
}
