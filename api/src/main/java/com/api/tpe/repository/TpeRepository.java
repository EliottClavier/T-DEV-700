package com.api.tpe.repository;

import com.api.tpe.model.Tpe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TpeRepository extends CrudRepository<Tpe, String> {
}