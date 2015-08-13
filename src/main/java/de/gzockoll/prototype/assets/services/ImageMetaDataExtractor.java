package de.gzockoll.prototype.assets.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Tag;
import com.google.common.collect.ImmutableList;
import de.gzockoll.prototype.assets.entity.MetaDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ImageMetaDataExtractor implements MetaDataExtractor {
    private static final Collection<String> supportedTypes= ImmutableList.of("image/jpeg");

    @Override
    public Map<String, String> extractMetaData(InputStream input, String mimeType) {
        try {
            return StreamSupport.stream(ImageMetadataReader.readMetadata(input).getDirectories().spliterator(), false)
                    .flatMap(d -> d.getTags().stream())
                    .collect(Collectors.toMap(t -> "[" + t.getDirectoryName() + "] " + t.getTagName(), Tag::getDescription));
        } catch (IOException | ImageProcessingException e) {
            log.debug("Error extracting Metadata " + e.getLocalizedMessage());
            return Collections.EMPTY_MAP;
        }
    }

    @Override
    public boolean supports(String mimeType) {
        return supportedTypes.contains(mimeType);

    }
}
