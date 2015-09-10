package com.vjoon.se.core.event;

import com.vjoon.se.core.entity.Asset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetCreatedEvent extends AssetEvent {

    public AssetCreatedEvent(Asset media) {
        super(media);
    }
}
