package de.gzockoll.prototype.assets.boundary;

import de.gzockoll.prototype.assets.entity.Media;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;

import java.util.Optional;

public class StreamHelper {
    public static HttpEntity<InputStreamResource> streamResult(Optional<Media> result) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (result.isPresent()) {
            httpHeaders.setContentType(MediaType.valueOf(result.get().getContentType()));
            httpHeaders.setContentDispositionFormData("attachment", result.get().getOriginalFilename());
            httpHeaders.setContentLength(result.get().getLength());
            return new ResponseEntity<>(new InputStreamResource(result.get().getInputStream()), httpHeaders, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
