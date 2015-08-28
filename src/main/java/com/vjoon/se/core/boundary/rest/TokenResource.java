package com.vjoon.se.core.boundary.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.util.Collection;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vjoon.se.core.boundary.StreamHelper;
import com.vjoon.se.core.control.TokenController;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.pojo.TokenType;
import com.vjoon.se.core.services.FileStore;

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

    @ApiOperation(value = "sends the asset belonging to this token as a byte stream",
            notes = "Original filetype and size will be set to support direct download with a web browser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "a byte stream") })
    @RequestMapping(method = RequestMethod.GET)
        return streamHelper.streamResult(fileStore, controller.resolve(id, TokenType.DOWNLOAD));
    public Token createToken(@RequestParam(value = "mediaId") @ApiParam(value = "Media Id to get a Token for",
            name = "mediaId", required = true) String mediaId, @RequestParam(value = "type") @ApiParam(name = "Type",
            value = "Mediatype to get a koten for") String type) {
        return controller.createToken(mediaId, type);
    @ApiOperation(value = "Get all Tokens", produces = "application/json")
    }

    @ApiOperation(value = "Download the Document with the given Id")
    @RequestMapping(value = "/token/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@ApiParam(name = "id", value = "Id of the Document",
            required = true) @PathVariable String id) throws IOException {
        return streamHelper.streamResult(fileStore, controller.resolve(id, TokenType.DOWNLOAD));
}
