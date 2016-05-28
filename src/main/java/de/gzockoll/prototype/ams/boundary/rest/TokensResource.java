package de.gzockoll.prototype.ams.boundary.rest;

import de.gzockoll.prototype.ams.boundary.StreamHelper;
import de.gzockoll.prototype.ams.control.TokenController;
import de.gzockoll.prototype.ams.pojo.Token;
import de.gzockoll.prototype.ams.services.FileStore;
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

    @ApiOperation(value = "creates a token for the given namespace and key", produces = "application/json")
    @RequestMapping(method = RequestMethod.POST)
    public Token createToken(
            @RequestParam(value = "namespace")
            @ApiParam(value = "namespace", name = "namespace", required = true)
            String namespace,

            @RequestParam(value = "key")
            @ApiParam(value = "key", name = "key", required = true)
            String key,

            @RequestParam(value = "type")
            @ApiParam(name = "type", value = "[download|upload]", required = true)
            String type,

            @RequestParam(value = "ttl", required = false)
            @ApiParam(name = "ttl", value = "Time-To-Live for the created token in seconds")
            Long ttl)
    {
        return controller.createToken(namespace,key , type, Optional.ofNullable(ttl));
    }

    @ApiOperation(value = "show all existing tokens", notes = "tokens will expire after a short time", produces = "application/json")
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Token> getAll() {
        return controller.findAll();
    }
}
