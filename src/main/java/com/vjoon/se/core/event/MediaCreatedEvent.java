package com.vjoon.se.core.event;

import de.gzockoll.prototype.assets.entity.Media;
import lombok.Data;

@Data
public class MediaCreatedEvent {
    private final Media media;
}
