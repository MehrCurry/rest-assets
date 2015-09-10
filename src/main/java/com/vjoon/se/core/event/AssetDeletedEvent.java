package com.vjoon.se.core.event;

import com.vjoon.se.core.entity.Asset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetDeletedEvent extends AssetEvent {

    public AssetDeletedEvent(Asset media) {
        super(media);
    }
}
