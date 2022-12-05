package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.service.TpeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/bank/tpe")
public class TpeController {

    @Autowired
    private TpeService tpeService;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> getAllTpe() {
        System.out.println("get all tpe");
        return new ResponseEntity<>(tpeService.getAll(), HttpStatus.OK);
    }

}
