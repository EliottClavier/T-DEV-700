package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.repository.CheckRepository;
import com.api.bank.service.CheckService;
import com.api.bank.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/bank/check")
public class CheckController {
    private final CheckService checkService;

    @Autowired
    public CheckController(CheckRepository checkRepository) {
        super();
        this.checkService = new CheckService(checkRepository);
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectResponse> add(@RequestBody QrCheck data){
        return ResponseEntity.ok(checkService.add(data));
    }
}
