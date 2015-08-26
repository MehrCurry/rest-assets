package de.gzockoll.prototype.assets.event;

import de.gzockoll.prototype.assets.entity.Media;
import lombok.Data;

@Data
public class MediaCreatedEvent {
    private final Media media;
}
