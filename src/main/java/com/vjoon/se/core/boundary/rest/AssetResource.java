package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.MediaController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

@Api(basePath = "/asset", value = "Asset", description = "Operations with a single Asset", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/asset") public class AssetResource {

    @Autowired private MediaController controller;

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove asset form production area",
            notes = "The database entry will only be set to existsInProduction=false")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "") })

    public HttpEntity deleteDocument(@PathVariable String id) throws IOException {
        checkArgument(id != null);
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
