package de.gzockoll.prototype.assets.events;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.entity.VaultType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CopyFinishedEvent {
    private final Media media;
    private final VaultType type;
}
