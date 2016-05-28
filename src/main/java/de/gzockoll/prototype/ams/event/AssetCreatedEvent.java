package de.gzockoll.prototype.ams.event;

import de.gzockoll.prototype.ams.entity.Asset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetCreatedEvent extends AssetEvent {

    public AssetCreatedEvent(Asset media) {
        super(media);
    }
}
