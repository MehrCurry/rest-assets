package de.gzockoll.prototype.assets.boundary.rest;

import de.gzockoll.prototype.assets.boundary.StreamHelper;
import de.gzockoll.prototype.assets.control.TokenController;
import de.gzockoll.prototype.assets.pojo.Token;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RestController
@Slf4j
public class TokenResource {
    @Autowired
    @Setter
    private TokenController controller;

    @Autowired
    private StreamHelper streamHelper;

    @RequestMapping(value = "/token/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@PathVariable String id) throws IOException {
            return streamHelper.streamResult(controller.resolve(id));
    }

    @RequestMapping(value= "/tokens", method = RequestMethod.POST, produces = "application/json")
    public Token createToken(@RequestParam(value = "mediaId", required = true) String mediaId) {
        return controller.createToken(mediaId);
    }

    @RequestMapping(value = "/tokens" , method = RequestMethod.GET, produces = "application/json")
    public Collection<Token> findAll() {
        return controller.findAll();
    }

}
