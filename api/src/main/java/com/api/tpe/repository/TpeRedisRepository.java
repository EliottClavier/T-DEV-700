package com.api.tpe.repository;

import com.api.tpe.model.TpeRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TpeRedisRepository extends CrudRepository<TpeRedis, String> {
}