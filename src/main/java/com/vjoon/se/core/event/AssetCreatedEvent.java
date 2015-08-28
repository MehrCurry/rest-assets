package com.vjoon.se.core.event;

import com.vjoon.se.core.entity.Asset;
import lombok.Data;

@Data
public class AssetCreatedEvent extends AssetEvent {

    public AssetCreatedEvent(Asset media) {
        super(media);
    }
}