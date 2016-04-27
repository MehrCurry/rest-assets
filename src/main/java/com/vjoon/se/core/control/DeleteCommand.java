package com.vjoon.se.core.control;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.services.FileStore;
import lombok.Data;

@Data
public class DeleteCommand implements Command {
    private final Asset asset;
    private final FileStore fileStore;

    @Override
    public void run() {
        asset.delete(fileStore);
    }
}
