package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.boundary.StreamHelper;
import com.vjoon.se.core.control.TokenController;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.pojo.TokenType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RestController @Slf4j public class TokenResource {

    @Autowired @Setter private TokenController controller;

    @Autowired private StreamHelper streamHelper;

    @RequestMapping(value = "/token/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@PathVariable String id) throws IOException {
        return streamHelper.streamResult(controller.resolve(id, TokenType.DOWNLOAD));
    }

    @RequestMapping(value = "/token?type={type}", method = RequestMethod.POST, produces = "application/json")
    public Token createToken(
            @RequestParam(value = "mediaId") String mediaId,
            @RequestParam(value = "type") String type) {
        return controller.createToken(mediaId,type);
    }

    @RequestMapping(value = "/tokens", method = RequestMethod.GET, produces = "application/json")
    public Collection<Token> findAll() {
        return controller.findAll();
    }

}
