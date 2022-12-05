package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Tpe;
import com.api.bank.service.TpeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bank/tpe")
public class TpeController {

    @Autowired
    private TpeService tpeService;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> getAllTpe() {
        ObjectResponse response = tpeService.getAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<ObjectResponse> register(@RequestBody Tpe tpe) {
        ObjectResponse response = tpeService.registerTpe(tpe);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @RequestMapping(path = "/{uuid}/whitelist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> whitelistTpe(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.updateTpeStatus(uuid, true);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @RequestMapping(path = "/{uuid}/blacklist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> blacklistTpe(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.updateTpeStatus(uuid, false);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @RequestMapping(path = "/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<ObjectResponse> deleteTpe(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.deleteByUUID(uuid);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @RequestMapping(path = "/{uuid}/status", method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> isWhitelistTpe(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.getTpeStatus(uuid);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
