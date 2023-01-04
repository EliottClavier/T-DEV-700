package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.service.TpeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bank/tpe")
public class TpeController {

    @Autowired
    private TpeService tpeService;

    /**
     * Get all TPE from the database
     *
     * @return ResponseEntity containing the list of TPE
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> getAllTpe() {
        ObjectResponse response = tpeService.getAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Get a TPE from the database
     *
     * @param uuid The UUID of the TPE
     * @return ResponseEntity containing the TPE
     */
    @RequestMapping(path = "/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> getTpeById(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.getTpeById(uuid);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Whitelist a TPE
     *
     * @param uuid The UUID of the TPE
     * @return ResponseEntity containing the TPE newly updated status
     */
    @RequestMapping(path = "/{uuid}/whitelist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> whitelistTpe(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.updateTpeStatus(uuid, true);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Blacklist a TPE
     *
     * @param uuid The UUID of the TPE
     * @return ResponseEntity containing the TPE newly updated status
     */
    @RequestMapping(path = "/{uuid}/blacklist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> blacklistTpe(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.updateTpeStatus(uuid, false);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Delete a TPE
     *
     * @param uuid The UUID of the TPE
     * @return ResponseEntity containing the status about TPE deletion
     */
    @RequestMapping(path = "/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<ObjectResponse> deleteTpe(@PathVariable("uuid") String uuid) {
        ObjectResponse response = tpeService.deleteByUUID(uuid);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
