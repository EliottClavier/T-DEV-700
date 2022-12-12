package com.api.bank.service;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class ShopService extends GenericService<Shop, ShopRepository> {

    @Autowired
    public ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        super(shopRepository);
    }

    public ObjectResponse getShopById(String id) {
        try {
            Shop shop = shopRepository.findById(UUID.fromString(id)).get();
            return new ObjectResponse("Shop found.", shop, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public ObjectResponse updateShopStatus(String id, Boolean whitelisted) {
        try {
            Shop shop = shopRepository.findById(UUID.fromString(id)).get();
            if (shop.getWhitelisted() != whitelisted) {
                shop.setWhitelisted(whitelisted);
                shopRepository.save(shop);
                shopRepository.flush();
                return new ObjectResponse("Success", shop, HttpStatus.OK);
            } else {
                return new ObjectResponse(String.format("Shop already %s", whitelisted ? "whitelisted" : "blacklisted"), HttpStatus.CONFLICT);
            }
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}

