package com.api.bank.repository;

import com.api.bank.model.entity.Shop;
import com.api.bank.model.entity.Tpe;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface ShopRepository extends GenericRepository<Shop> {
    Optional<Shop> findByName(String name);
    Boolean existsByName(String name);

    @Transactional
    void deleteByName(String name);
}