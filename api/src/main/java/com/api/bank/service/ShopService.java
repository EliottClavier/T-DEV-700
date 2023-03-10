package com.api.bank.service;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Shop;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShopService extends GenericService<Shop> {
    private final PasswordEncoder passwordEncoder;
    private final ShopRepository shopRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public ShopService(
            PasswordEncoder passwordEncoder,
            ShopRepository shopRepository,
            AccountRepository accountRepository
    ) {
        super(shopRepository);
        this.passwordEncoder = passwordEncoder;
        this.shopRepository = shopRepository;
        this.accountRepository = accountRepository;
    }


    /**
     * Get Shop by id (UUID)
     * @param id Shop UUID
     * @return the Shop if found, exception otherwise
     */
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

    /**
     * Update Shop status by id (UUID)
     * @param id Shop UUID
     * @param whitelisted Shop new status (whitelist or blacklist)
     * @return the Shop with its new status if found and not already with the asked status, exception otherwise
     */
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

    /**
     * Register Shop
     * @param shop Shop data
     * @return the Shop if registered, null otherwise
     */
    public Shop registerShop(Shop shop) {
        if (!shopRepository.existsByName(shop.getName())) {
            String encodedPass = passwordEncoder.encode(shop.getPassword());
            shop.setPassword(encodedPass);
            shop.setWhitelisted(false);
            return shopRepository.save(shop);
        }
        return null;
    }

    /**
     * Delete Shop from database, including Account and Client linked to it
     * @param id Shop UUID
     * @return a success message if deleted, exception otherwise
     */
    public ObjectResponse deleteShopStackByUUID(String id) {
        try {
            // Delete shop
            shopRepository.deleteById(UUID.fromString(id));
            shopRepository.flush();

            // Delete account
            Account account = accountRepository.findAccountByClient_Id(UUID.fromString(id));
            accountRepository.deleteById(account.getId());
            return new ObjectResponse("Success", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public Shop getShopByName(String name) {
        try {
            return shopRepository.findByName(name).get();
        } catch (Exception e) {
            return null;
        }
    }
}

