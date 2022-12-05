package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Tpe;
import com.api.bank.service.TpeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        ObjectResponse response = tpeService.add(tpe);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @RequestMapping(path = "/{mac}/whitelist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> whitelistTpe(@PathVariable("mac") String mac) {
        ObjectResponse response = tpeService.updateTpeStatus(mac, true);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @RequestMapping(path = "/{mac}/blacklist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> blacklistTpe(@PathVariable("mac") String mac) {
        ObjectResponse response = tpeService.updateTpeStatus(mac, false);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
