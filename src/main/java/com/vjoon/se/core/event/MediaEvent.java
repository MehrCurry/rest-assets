package com.vjoon.se.core.event;

import com.vjoon.se.core.entity.Media;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class  MediaEvent {
    protected final Media media;
}
