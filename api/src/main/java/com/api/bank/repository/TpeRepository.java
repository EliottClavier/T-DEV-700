package com.api.bank.repository;

import com.api.bank.model.entity.Tpe;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TpeRepository extends GenericRepository<Tpe> {
    Tpe findByMac(String mac);

    Boolean existsByMac(String mac);
}
