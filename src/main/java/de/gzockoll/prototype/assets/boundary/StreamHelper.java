package de.gzockoll.prototype.assets.boundary;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.services.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class StreamHelper {

    @Autowired
    private FileStore fileStore;

    public ResponseEntity<InputStreamResource> streamResult(Optional<Object> result) throws FileNotFoundException {
        checkArgument(!result.isPresent() || result.get().getClass().isAssignableFrom(Media.class));
        if (result.isPresent()) {
            return streamResult((Media) result.get());
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<InputStreamResource> streamResult(Media media) throws FileNotFoundException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(media.getContentType()));
        httpHeaders.setContentDispositionFormData("attachment", media.getOriginalFilename());
        httpHeaders.setContentLength(media.getLength());
        return new ResponseEntity<>(new InputStreamResource(media.getStream(fileStore)), httpHeaders, HttpStatus.OK);
    }
}
