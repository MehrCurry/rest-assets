package de.gzockoll.prototype.assets.services;

import de.gzockoll.prototype.assets.entity.MetaDataExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;


@Service
public class MetaDataExtractorFactory {

    @Autowired
    Set<MetaDataExtractor> extractors;

    public Optional<MetaDataExtractor> extractorFor(String mimeType) {
        return extractors.stream().filter(e -> e.supports(mimeType)).findFirst();
    }
}
