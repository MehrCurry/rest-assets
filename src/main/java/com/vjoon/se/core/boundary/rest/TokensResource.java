package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.boundary.StreamHelper;
import com.vjoon.se.core.control.TokenController;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.services.FileStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@Api(basePath = "/tokens", value = "Tokens", description = "Operations with tokens", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/tokens") public class TokensResource {

    @Autowired
    @Setter
    private TokenController controller;

    @Autowired
    private StreamHelper streamHelper;

    @Autowired
    @Qualifier("production")
    private FileStore fileStore;

    @ApiOperation(value = "creates a token for the given mediaID", notes = "mediaID will be checked.", produces = "application/json")
    @RequestMapping(method = RequestMethod.POST)
    public Token createToken(
            @RequestParam(value = "mediaId")
            @ApiParam(value = "Asset Id to get a Token for", name = "mediaId", required = true)
            String mediaId,

            @RequestParam(value = "type")
            @ApiParam(name = "type", value = "[download|upload]")
            String type,

            @RequestParam(value = "ttl", required = false)
            @ApiParam(name = "ttl", value = "Time-To-Live for the created token in seconds")
            Long ttl)
    {
        return controller.createToken(mediaId, type, Optional.ofNullable(ttl));
    }

    @ApiOperation(value = "show all existing tokens", notes = "tokens will expire after a short time", produces = "application/json")
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Token> getAll() {
        return controller.findAll();
    }
}
