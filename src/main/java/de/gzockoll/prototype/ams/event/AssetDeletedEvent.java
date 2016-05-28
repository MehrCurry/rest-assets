package de.gzockoll.prototype.ams.event;

import de.gzockoll.prototype.ams.entity.Asset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetDeletedEvent extends AssetEvent {

    public AssetDeletedEvent(Asset media) {
        super(media);
    }
}
