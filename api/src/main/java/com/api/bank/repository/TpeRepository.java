package com.api.bank.repository;

import com.api.bank.model.entity.Tpe;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface TpeRepository extends GenericRepository<Tpe> {
    Optional<Tpe> findByMac(String mac);

    Boolean existsByMac(String mac);
}
