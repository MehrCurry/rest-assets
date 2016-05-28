package de.gzockoll.prototype.ams.boundary.rest;

import de.gzockoll.prototype.ams.control.SnapshotController;
import de.gzockoll.prototype.ams.entity.Snapshot;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(basePath = "/snapshot", value = "Snapshot", description = "Operations with a single Snapshots", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/snapshot") public class SnapshotResource {

    @Autowired
    private SnapshotController controller;

    @ApiOperation("Deletes the Snapshot with the given Id")
    @RequestMapping(value = "/snapshot/{id}", method = RequestMethod.DELETE)
    public HttpEntity delete(@PathVariable Long id) {
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("Creates a new snapshot, an resets the production area to the state, defined by ths snapshot with the given id")
    @RequestMapping(value = "/snapshot/{id}", method = RequestMethod.GET)
    public Snapshot restore(@PathVariable Long id, @RequestParam(required = false) Boolean restore) {
        return controller.restore(id, restore!=null? restore : false);
    }
}
