package de.gzockoll.prototype.assets.boundary.rest;

import de.gzockoll.prototype.assets.boundary.StreamHelper;
import de.gzockoll.prototype.assets.control.TokenController;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.pojo.Token;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/token")
@Slf4j
public class TokenResource {
    @Autowired
    @Setter
    private TokenController controller;

    @Autowired
    @Setter
    private MediaRepository mediaRepository;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public Token createToken(@RequestParam(value = "mediaId", required = true) String mediaId) {
        return controller.createToken(mediaId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@PathVariable String id) throws IOException {
            Optional<Media> result =controller.resolve(id).map(mediaId -> mediaRepository.findByMediaId(mediaId).stream().findFirst().get());

            return StreamHelper.streamResult(result);
    }
}
