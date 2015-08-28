package com.vjoon.se.core.event;

import com.vjoon.se.core.entity.Asset;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class AssetEvent {
    protected final Asset media;
}
