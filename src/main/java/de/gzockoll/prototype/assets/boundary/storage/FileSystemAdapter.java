package de.gzockoll.prototype.assets.boundary.storage;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.entity.StorageAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;

public class FileSystemAdapter implements StorageAdapter {
    @Override
    public InputStream getStream(Media media) {
        checkArgument(media.isExistsInArchive());
        String prefix="assets" + File.separator + "archive" + File.separator;

        try {
            return new FileInputStream(new File(prefix+media.getFilename()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
