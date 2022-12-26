package com.api.gateway.transaction.controller;


import com.api.gateway.transaction.model.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/transaction-request-redis")
public class TransactionRequestRedisController {

    public static final String HASH_KEY_NAME = "TRANSACTION";
    public static final Long TTl_HASH_KEY = 10L;

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<?> getAllTransactionRequest() {
        Set<String> keys = customRedisTemplate.keys(HASH_KEY_NAME + ":*");
        return new ResponseEntity<>(customRedisTemplate.opsForValue().multiGet(keys), HttpStatus.OK);
    }

    @RequestMapping(path = "/{transactionId}", method = RequestMethod.GET)
    public ResponseEntity<?> getTransactionRequest(@PathVariable("transactionId") String id) {
        return new ResponseEntity<>(customRedisTemplate.opsForValue().get(HASH_KEY_NAME + ":" + id), HttpStatus.OK);
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<String> addTransactionRequest(@RequestBody TransactionRequest transactionRequest) {
        transactionRequest.setId(UUID.randomUUID().toString());
        if (customRedisTemplate.hasKey(HASH_KEY_NAME + ":" + transactionRequest.getId())) {
            return new ResponseEntity<>("Transaction request already present.", HttpStatus.CONFLICT);
        } else if (transactionRequest.isValid()) {
            customRedisTemplate.opsForValue().set(
                    HASH_KEY_NAME + ":" + transactionRequest.getId(), transactionRequest.toString(),
                    TTl_HASH_KEY, TimeUnit.MINUTES
            );
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, transactionRequest.getId(), transactionRequest.toString());
            return new ResponseEntity<>("Transaction request saved.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction request not valid.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{transactionId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTransactionRequest(
            @PathVariable("transactionId") String id, @RequestBody TransactionRequest transactionRequest
    ) {
        if (customRedisTemplate.hasKey(HASH_KEY_NAME + ":" + id) && transactionRequest.isValid()) {
            customRedisTemplate.opsForValue().set(
                    HASH_KEY_NAME + ":" + id, transactionRequest.toString(),
                    TTl_HASH_KEY, TimeUnit.MINUTES
            );
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, transactionRequest.getId(), transactionRequest.toString());
            return new ResponseEntity<>("Transaction request updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction request not found.", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeAll() {
        customRedisTemplate.delete(HASH_KEY_NAME);
        customRedisTemplate.opsForHash().delete(HASH_KEY_NAME);
        return new ResponseEntity<>("All transactions removed.", HttpStatus.OK);
    }

    @RequestMapping(path = "/{transactionId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeTransactionRequest(@PathVariable("transactionId") String id) {
        if (customRedisTemplate.hasKey(HASH_KEY_NAME + ":" + id)) {
            customRedisTemplate.delete(HASH_KEY_NAME + ":" + id);
            customRedisTemplate.opsForHash().delete(HASH_KEY_NAME, id);
            return new ResponseEntity<>("Transaction request ended.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction request not found.", HttpStatus.NOT_FOUND);
        }
    }
}
