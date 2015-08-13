package de.gzockoll.prototype.assets.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Tag;
import com.google.common.collect.ImmutableList;
import de.gzockoll.prototype.assets.entity.MetaDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
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
public class PdfMetaDataExtractor implements MetaDataExtractor {
    private static final Collection<String> supportedTypes= ImmutableList.of("application/pdf");

    @Override
    public Map<String, String> extractMetaData(InputStream input, String mimeType) {
        try (PDDocument doc=PDDocument.load(input)) {
            PDDocumentInformation info = doc.getDocumentInformation();
            return info.getMetadataKeys().stream().collect(Collectors.toMap(k -> k, k -> info.getPropertyStringValue(k).toString()));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean supports(String mimeType) {
        return supportedTypes.contains(mimeType);

    }
}
