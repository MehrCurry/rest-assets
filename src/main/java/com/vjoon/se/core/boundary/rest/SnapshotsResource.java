package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.SnapshotController;
import com.vjoon.se.core.entity.NameSpace;
import com.vjoon.se.core.entity.Snapshot;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(basePath = "/snapshots", value = "Snapshots", description = "Operations with Snapshots", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/snapshots") public class SnapshotsResource {

    @Autowired private SnapshotController controller;

    @ApiOperation(value = "creates a snapshot",
            notes = "All assets currently in the production area will be part of that snapshot")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "") })
    @RequestMapping(method = RequestMethod.POST) public HttpEntity create(
            @RequestParam(value = "namespace")
            @ApiParam(value = "namespace", name = "namespace", required = true)
            String namespace
    ) {
        controller.create(new NameSpace(namespace));
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Removes all snapshots",
            notes = "Use with extreme care")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "") })
    @RequestMapping(method = RequestMethod.DELETE) public HttpEntity deleteAll(
            @RequestParam(value = "namespace")
            @ApiParam(value = "namespace", name = "namespace", required = true)
            String namespace
    ) {
        controller.deleteAll(new NameSpace(namespace));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Retrieve all snapshots")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all snapshots") })
    @RequestMapping(method = RequestMethod.GET, produces = "application/json") public @ResponseBody
    List<Snapshot> findAll(
            @RequestParam(value = "namespace")
            @ApiParam(value = "namespace", name = "namespace", required = true)
            String namespace
    ) {
        return controller.findAll(new NameSpace(namespace));
    }
}
