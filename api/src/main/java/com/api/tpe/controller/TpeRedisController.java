package com.api.tpe.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import com.api.tpe.model.Message;
import com.api.tpe.model.MessageType;
import com.api.tpe.model.TpeRedis;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/tpe-manager/tpe")
public class TpeRedisController {

    public static final String HASH_KEY_NAME = "TPE";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private TpeRepository tpeRepository;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<?> getAllTpe() {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().values(HASH_KEY_NAME), HttpStatus.OK);
    }

    @RequestMapping(path = "/{mac}", method = RequestMethod.GET)
    public ResponseEntity<?> getTpe(@PathVariable("mac") String mac) {
        return new ResponseEntity<>(customRedisTemplate.opsForHash().get(HASH_KEY_NAME, mac), HttpStatus.OK);
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<String> addTpe(@RequestBody TpeRedis tpeRedis) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, tpeRedis.getId())) {
            return new ResponseEntity<>("TPE already registered.", HttpStatus.CONFLICT);
        } else if (tpeRedis.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, tpeRedis.getId(), tpeRedis.getIp());
            return new ResponseEntity<>("TPE registered.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("TPE not valid.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{mac}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTpe(@PathVariable("mac") String mac, @RequestBody TpeRedis tpeRedis) {
        if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, mac) && tpeRedis.isValid()) {
            customRedisTemplate.opsForHash().put(HASH_KEY_NAME, mac, tpeRedis.getIp());
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

    @MessageMapping("/connect")
    public void sendTpe(@RequestBody String tpeString) {
        String responseDestination = "/public/connect";
        Gson gson = new Gson();
        Message message = new Message("Processing", MessageType.ERROR);

        try {
            TpeRedis tpeRedis = gson.fromJson(tpeString, TpeRedis.class);
            Tpe tpe = tpeRepository.findByMac(tpeRedis.getId()).get();

            if (tpe.getWhitelisted()) {
                if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, tpeRedis.getId())) {
                    message.setMessage("TPE already registered.");
                } else if (tpeRedis.isValid()) {
                    customRedisTemplate.opsForHash().put(HASH_KEY_NAME, tpeRedis.getId(), tpeRedis.getIp());
                    message.setMessage("TPE registered.");
                    message.setType(MessageType.SUCCESS);
                } else {
                    message.setMessage("TPE not valid.");
                }
            } else {
                message.setMessage("TPE not whitelisted.");
            }
        } catch (IllegalArgumentException e) {
            message.setMessage("TPE not found.");
        } catch (Exception e) {
            message.setMessage("Error registring TPE.");
        }
        this.simpMessagingTemplate.convertAndSend(responseDestination, message);
    }
}
