package com.vjoon.se.core.event;

import com.vjoon.se.core.entity.Media;
import lombok.Data;

@Data
public class MediaDeletedEvent extends MediaEvent {

    public MediaDeletedEvent(Media media) {
        super(media);
    }
}
