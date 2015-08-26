package com.vjoon.se.core.event;

import com.vjoon.se.core.entity.Media;
import lombok.Data;

@Data
public class MediaCreatedEvent extends MediaEvent {

    public MediaCreatedEvent(Media media) {
        super(media);
    }
}
