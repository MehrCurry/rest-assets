package de.gzockoll.prototype.ams.event;

import de.gzockoll.prototype.ams.entity.Asset;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class AssetEvent {
    protected final Asset media;
}
