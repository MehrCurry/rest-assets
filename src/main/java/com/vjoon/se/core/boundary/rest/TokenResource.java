package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.boundary.StreamHelper;
import com.vjoon.se.core.control.TokenController;
import com.vjoon.se.core.pojo.TokenType;
import com.vjoon.se.core.services.FileStore;
import io.swagger.annotations.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation(value = "sends the asset belonging to this token as a byte stream",
            notes = "Original filetype and size will be set to support direct download with a web browser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "a byte stream") })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocumentWithToken(
            @ApiParam(name = "id", value = "Id of the token",
            required = true)
            @PathVariable String id) throws IOException {
        return streamHelper.streamResult(fileStore, controller.resolve(id, TokenType.DOWNLOAD));
    }
}
