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
import java.util.List;

@RestController
@RequestMapping("/tokens")
@Slf4j
public class TokensResource {
    @Autowired
    @Setter
    private TokenController controller;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public Token createToken(@RequestParam(value = "mediaId", required = true) String mediaId) {
        return controller.createToken(mediaId);
    }


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public Collection<Token> findAll() {
        return controller.findAll();
    }
}
