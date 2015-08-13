package de.gzockoll.prototype.assets.entity;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by gzockoll on 13/08/15.
 */
public interface MetaDataExtractor {
    Map<String, String> extractMetaData(InputStream input, String mimeType);

    boolean supports(String mimeType);
}
