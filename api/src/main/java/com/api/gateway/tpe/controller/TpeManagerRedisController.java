package com.api.gateway.tpe.controller;


import com.api.gateway.tpe.model.TpeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;

@RestController
@RequestMapping(path = "/tpe-manager-redis")
public class TpeManagerRedisController {

    /*
     * Controller used to test Redis, precisely the TpeManager part using Redis Hash
     */

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    /**
     * Used to get all TPE from Redis
     *
     * @return array of TPE instances from Redis
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<?> getAllTpe() {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().values(HASH_KEY_NAME_TPE), HttpStatus.OK);
    }

    /**
     * Used to get a TPE from Redis by ID
     *
     * @param androidId the id of the TPE to get
     * @return TPE instance from Redis
     */
    @RequestMapping(path = "/{androidId}", method = RequestMethod.GET)
    public ResponseEntity<?> getTpe(@PathVariable("androidId") String androidId) {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TPE, androidId), HttpStatus.OK);
    }

    /**
     * Used to create a TPE in Redis
     *
     * @param tpeManager the TPE to create
     * @return ResponseEntity instance giving details about the TPE creation status
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<String> addTpe(@RequestBody TpeManager tpeManager) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeManager.getUsername())) {
            return new ResponseEntity<>("TPE already registered.", HttpStatus.CONFLICT);
        } else if (tpeManager.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TPE, tpeManager.getUsername(), tpeManager.getUsername());
            return new ResponseEntity<>("TPE registered.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not valid.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Used to update a TPE in Redis
     *
     * @param androidId the TPE ID to update
     * @param tpeManager the TPE data
     * @return ResponseEntity instance giving details about the TPE update status
     */
    @RequestMapping(path = "/{androidId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTpe(@PathVariable("androidId") String androidId, @RequestBody TpeManager tpeManager) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, androidId) && tpeManager.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TPE, androidId, tpeManager.getUsername());
            return new ResponseEntity<>("TPE updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not found.", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Used to delete all TPE in Redis
     *
     * @return ResponseEntity instance giving details about the TPE delete status
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeAll() {
        customRedisTemplate.delete(HASH_KEY_NAME_TPE);
        return new ResponseEntity<>("All TPE removed.", HttpStatus.OK);
    }

    /**
     * Used to delete a TPE in Redis by ID
     *
     * @param androidId the TPE ID to delete
     * @return ResponseEntity instance giving details about the TPE delete status
     */
    @RequestMapping(path = "/{androidId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeTpe(@PathVariable("androidId") String androidId) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, androidId)) {
            customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, androidId);
            return new ResponseEntity<>("TPE removed.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not found.", HttpStatus.NOT_FOUND);
        }
    }
}
