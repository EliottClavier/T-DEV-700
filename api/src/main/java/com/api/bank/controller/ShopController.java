package com.api.bank.controller;

import com.api.bank.model.ObjectResponse;
import com.api.bank.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/bank/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * Get all Shop from the database
     *
     * @return ResponseEntity containing the list of Shop
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> getAllShop() {
        ObjectResponse response = shopService.getAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Get a Shop from the database
     *
     * @param uuid The UUID of the Shop
     * @return ResponseEntity containing the Shop
     */
    @RequestMapping(path = "/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ObjectResponse> getShopById(@PathVariable("uuid") String uuid) {
        ObjectResponse response = shopService.getShopById(uuid);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Whitelist a Shop
     *
     * @param uuid The UUID of the Shop
     * @return ResponseEntity containing the Shop newly updated status
     */
    @RequestMapping(path = "/{uuid}/whitelist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> whitelistShop(@PathVariable("uuid") String uuid) {
        ObjectResponse response = shopService.updateShopStatus(uuid, true);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Blacklist a Shop
     *
     * @param uuid The UUID of the Shop
     * @return ResponseEntity containing the Shop newly updated status
     */
    @RequestMapping(path = "/{uuid}/blacklist", method = RequestMethod.PUT)
    public ResponseEntity<ObjectResponse> blacklistShop(@PathVariable("uuid") String uuid) {
        ObjectResponse response = shopService.updateShopStatus(uuid, false);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Delete a Shop
     *
     * @param uuid The UUID of the Shop
     * @return ResponseEntity containing the status about Shop deletion
     */
    @RequestMapping(path = "/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<ObjectResponse> deleteShop(@PathVariable("uuid") String uuid) {
        ObjectResponse response = shopService.deleteShopStackByUUID(uuid);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
