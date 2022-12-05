package com.api.tpe.controller;

import com.api.tpe.model.Tpe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/tpe")
public class TpeController {

    public static final String HASH_KEY_NAME = "TPE";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getAllTpe() {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().values(HASH_KEY_NAME), HttpStatus.OK);
    }

    @RequestMapping(path = "/{mac}", method = RequestMethod.GET)
    public ResponseEntity<?> getTpe(@PathVariable("mac") String mac) {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().get(HASH_KEY_NAME, mac), HttpStatus.OK);
    }

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<String> addTpe(@RequestBody Tpe tpe) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, tpe.getId())) {
            return new ResponseEntity<>("TPE already registered.", HttpStatus.CONFLICT);
        } else if (tpe.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, tpe.getId(), tpe.getIp());
            return new ResponseEntity<>("TPE registered.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not valid.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{mac}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTpe(@PathVariable("mac") String mac, @RequestBody Tpe tpe) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, mac) && tpe.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, tpe.getId(), tpe.getIp());
            return new ResponseEntity<>("TPE updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not found.", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{mac}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeTpe(@PathVariable("mac") String mac) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, mac)) {
            customRedisTemplate.opsForHash().delete(HASH_KEY_NAME, mac);
            return new ResponseEntity<>("TPE removed.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not found.", HttpStatus.NOT_FOUND);
        }
    }
}
