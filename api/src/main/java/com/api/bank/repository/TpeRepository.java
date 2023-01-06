package com.api.bank.repository;

import com.api.bank.model.entity.Tpe;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface TpeRepository extends GenericRepository<Tpe> {
    Optional<Tpe> findByAndroidId(String id);
    Boolean existsByAndroidId(String id);
    @Transactional
    void deleteByAndroidId(String id);
}
