package com.api.transaction.tpe.repository;

import com.api.transaction.tpe.model.TpeManager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TpeManagerRepository extends CrudRepository<TpeManager, String> {
}