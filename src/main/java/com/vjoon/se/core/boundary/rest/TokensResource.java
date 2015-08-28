package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.boundary.StreamHelper;
import com.vjoon.se.core.control.TokenController;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.pojo.TokenType;
import com.vjoon.se.core.services.FileStore;
import io.swagger.annotations.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Api(basePath = "/tokens", value = "Token", description = "Operations with Tokens", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/token") public class TokensResource {

    @Autowired
    @Setter
    private TokenController controller;

    @Autowired
    private StreamHelper streamHelper;

    @Autowired
    @Qualifier("production")
    private FileStore fileStore;

    @ApiOperation(value = "creates a token for the given mediaID", notes = "mediaID will be checked", produces = "application/json")
    @RequestMapping(method = RequestMethod.POST)
    public Token createToken(@RequestParam(value = "mediaId") @ApiParam(value = "Media Id to get a Token for",
            name = "mediaId", required = true) String mediaId, @RequestParam(value = "type") @ApiParam(name = "Type",
            value = "Mediatype to get a koten for") String type) {
        return controller.createToken(mediaId, type);
    }

    @ApiOperation(value = "show all existing tokens", notes = "tokens will expire after a short time", produces = "application/json")
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Token> getAll() {
        return controller.findAll();
    }
}
