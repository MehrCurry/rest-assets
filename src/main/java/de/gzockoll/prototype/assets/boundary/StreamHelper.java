package de.gzockoll.prototype.assets.boundary;

import de.gzockoll.prototype.assets.entity.Media;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;

import java.io.FileNotFoundException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class StreamHelper {
    public static ResponseEntity<InputStreamResource> streamResult(Optional<Object> result) throws FileNotFoundException {
        checkArgument(!result.isPresent() || result.get().getClass().isAssignableFrom(Media.class));
        if (result.isPresent()) {
            return streamResult((Media) result.get());
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<InputStreamResource> streamResult(Media media) throws FileNotFoundException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(media.getContentType()));
        httpHeaders.setContentDispositionFormData("attachment", media.getOriginalFilename());
        httpHeaders.setContentLength(media.getLength());
        return new ResponseEntity<>(new InputStreamResource(media.getInputStream()), httpHeaders, HttpStatus.OK);
    }
}
