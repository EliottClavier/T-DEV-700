package com.api.auth.security.details.service;

import com.api.bank.model.entity.Shop;
import com.api.bank.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class ShopDetailsService implements UserDetailsService {
    @Autowired
    private ShopRepository shopRepository;

    // Process to check if shop exists in database, is whitelisted and give it the role of shop
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException, AccessDeniedException {
        Optional<Shop> shopRes = shopRepository.findByName(name);
        if(shopRes.isEmpty())
            throw new UsernameNotFoundException(String.format("Could not find Shop with Android ID %s.", name));
        Shop shop = shopRes.get();
        if (!shop.getWhitelisted()) {
            throw new AccessDeniedException("Not whitelisted.");
        }
        return new org.springframework.security.core.userdetails.User(shop.getName(), shop.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SHOP")));
    }
}
