package com.api.gateway.tpe.controller;


import com.api.gateway.tpe.model.TpeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/tpe-manager-redis")
public class TpeManagerRedisController {

    public static final String HASH_KEY_NAME = "TPE";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<?> getAllTpe() {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().values(HASH_KEY_NAME), HttpStatus.OK);
    }

    @RequestMapping(path = "/{androidId}", method = RequestMethod.GET)
    public ResponseEntity<?> getTpe(@PathVariable("androidId") String androidId) {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().get(HASH_KEY_NAME, androidId), HttpStatus.OK);
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<String> addTpe(@RequestBody TpeManager tpeManager) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, tpeManager.getUsername())) {
            return new ResponseEntity<>("TPE already registered.", HttpStatus.CONFLICT);
        } else if (tpeManager.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, tpeManager.getUsername(), tpeManager.getUsername());
            return new ResponseEntity<>("TPE registered.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not valid.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{androidId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTpe(@PathVariable("androidId") String androidId, @RequestBody TpeManager tpeManager) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, androidId) && tpeManager.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, androidId, tpeManager.getUsername());
            return new ResponseEntity<>("TPE updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not found.", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeAll() {
        customRedisTemplate.delete(HASH_KEY_NAME);
        return new ResponseEntity<>("All TPE removed.", HttpStatus.OK);
    }

    @RequestMapping(path = "/{androidId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeTpe(@PathVariable("androidId") String androidId) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, androidId)) {
            customRedisTemplate.opsForHash().delete(HASH_KEY_NAME, androidId);
            return new ResponseEntity<>("TPE removed.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not found.", HttpStatus.NOT_FOUND);
        }
    }
}
