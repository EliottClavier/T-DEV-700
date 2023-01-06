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

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_TTL_TRANSACTION;

@RestController
@RequestMapping(path = "/transaction-request-redis")
public class TransactionRequestRedisController {

    /*
    * Controller used to test Redis, precisely the transaction request part using Redis Hash and TTL
     */

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    /**
     * Used to get all transaction requests from Redis
     *
     * @return array of TransactionRequest instances from Redis
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<?> getAllTransactionRequest() {
        Set<String> keys = customRedisTemplate.keys(HASH_KEY_NAME_TRANSACTION + ":*");
        return new ResponseEntity<>(customRedisTemplate.opsForValue().multiGet(keys), HttpStatus.OK);
    }

    /**
     * Used to get a transaction request from Redis by ID
     *
     * @param id the id of the transaction request to get
     * @return TransactionRequest instance from Redis
     */
    @RequestMapping(path = "/{transactionId}", method = RequestMethod.GET)
    public ResponseEntity<?> getTransactionRequest(@PathVariable("transactionId") String id) {
        return new ResponseEntity<>(customRedisTemplate.opsForValue().get(HASH_KEY_NAME_TRANSACTION + ":" + id), HttpStatus.OK);
    }

    /**
     * Used to create a transaction request in Redis
     *
     * @param transactionRequest the transaction request to create
     * @return ResponseEntity instance giving details about the transaction request creation status
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<String> addTransactionRequest(@RequestBody TransactionRequest transactionRequest) {
        transactionRequest.setId(UUID.randomUUID().toString());
        if (customRedisTemplate.hasKey(HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId())) {
            return new ResponseEntity<>("Transaction request already present.", HttpStatus.CONFLICT);
        } else if (transactionRequest.isValid()) {
            customRedisTemplate.opsForValue().set(
                    HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId(), transactionRequest.toString(),
                    HASH_KEY_TTL_TRANSACTION, TimeUnit.MINUTES
            );
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId(), transactionRequest.toString());
            return new ResponseEntity<>("Transaction request saved.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction request not valid.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Used to update a transaction request in Redis
     *
     * @param id the transaction request ID to update
     * @param transactionRequest the transaction request data
     * @return ResponseEntity instance giving details about the transaction request update status
     */
    @RequestMapping(path = "/{transactionId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTransactionRequest(
            @PathVariable("transactionId") String id, @RequestBody TransactionRequest transactionRequest
    ) {
        if (customRedisTemplate.hasKey(HASH_KEY_NAME_TRANSACTION + ":" + id) && transactionRequest.isValid()) {
            customRedisTemplate.opsForValue().set(
                    HASH_KEY_NAME_TRANSACTION + ":" + id, transactionRequest.toString(),
                    HASH_KEY_TTL_TRANSACTION, TimeUnit.MINUTES
            );
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId(), transactionRequest.toString());
            return new ResponseEntity<>("Transaction request updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction request not found.", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Used to delete all transactions requests in Redis
     *
     * @return ResponseEntity instance giving details about the transaction request delete status
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeAll() {
        customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION);
        customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION);
        return new ResponseEntity<>("All transactions removed.", HttpStatus.OK);
    }

    /**
     * Used to delete a transaction request in Redis by ID
     *
     * @param id the transaction request ID to delete
     * @return ResponseEntity instance giving details about the transaction request delete status
     */
    @RequestMapping(path = "/{transactionId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeTransactionRequest(@PathVariable("transactionId") String id) {
        if (customRedisTemplate.hasKey(HASH_KEY_NAME_TRANSACTION + ":" + id)) {
            customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + id);
            customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, id);
            return new ResponseEntity<>("Transaction request ended.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Transaction request not found.", HttpStatus.NOT_FOUND);
        }
    }
}
