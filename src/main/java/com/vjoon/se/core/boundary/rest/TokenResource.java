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

@Api(basePath = "/token", value = "Token", description = "Operations with a single Token", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/token") public class TokenResource {

    @Autowired
    @Setter
    private TokenController controller;

    @Autowired
    private StreamHelper streamHelper;

    @Autowired
    @Qualifier("production")
    private FileStore fileStore;

    @ApiOperation(value = "Get all Tokens", produces = "application/json")
    @RequestMapping(method = RequestMethod.GET)
    public Token createToken(@RequestParam(value = "mediaId") @ApiParam(value = "Media Id to get a Token for",
            name = "mediaId", required = true) String mediaId, @RequestParam(value = "type") @ApiParam(name = "Type",
            value = "Mediatype to get a koten for") String type) {
        return controller.createToken(mediaId, type);
    }

    @ApiOperation(value = "sends the asset belonging to this token as a byte stream",
            notes = "Original filetype and size will be set to support direct download with a web browser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "a byte stream") })
    @RequestMapping(value = "/token/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@ApiParam(name = "id", value = "Id of the Document",
            required = true) @PathVariable String id) throws IOException {
        return streamHelper.streamResult(fileStore, controller.resolve(id, TokenType.DOWNLOAD));
    }
}
