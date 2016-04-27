package com.vjoon.se.core.control;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.services.FileStore;
import lombok.Data;

@Data
public class CopyCommand implements Command {
    private final Asset asset;
    private final FileStore source;
    private final FileStore destination;

    @Override
    public void run() {
        asset.copy(source,destination);
    }
}
