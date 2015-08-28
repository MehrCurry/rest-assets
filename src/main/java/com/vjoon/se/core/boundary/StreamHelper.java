package com.vjoon.se.core.boundary;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.services.FileStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Service public class StreamHelper {

    public ResponseEntity<InputStreamResource> streamResult(FileStore fileStore,Optional<Object> result) throws FileNotFoundException {
        checkArgument(!result.isPresent() || result.get().getClass().isAssignableFrom(Asset.class));
        if (result.isPresent()) {
            return streamResult(fileStore,(Asset) result.get());
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<InputStreamResource> streamResult(FileStore fileStore,Asset media) throws FileNotFoundException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(media.getContentType()));
        httpHeaders.setContentDispositionFormData("attachment", media.getOriginalFilename());
        httpHeaders.setContentLength(media.getLength());
        return new ResponseEntity<>(new InputStreamResource(media.getStream(fileStore)), httpHeaders, HttpStatus.OK);
    }
}
