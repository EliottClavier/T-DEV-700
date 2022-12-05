package com.api.bank.service;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Base;
import com.api.bank.repository.GenericRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class GenericService<T extends Base, T1 extends GenericRepository<T>> {


    private T1 repository;

    private T entity;

    public GenericService(T1 repository) {
        this.repository = repository;
    }
    public GenericService() {
        this.repository = null;
    }


    public ObjectResponse add(T entity) {
        try {
            repository.save(entity);
            repository.flush();

            return new ObjectResponse("Success", entity, true);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), false);
        }
    }

    public ObjectResponse remove(T entity) {
        try {
            repository.delete(entity);
            repository.flush();
            return new ObjectResponse("Success", true);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), false);
        }
    }

    public ObjectResponse update(T entity) {
        try {
            repository.save(entity);
            repository.flush();
            return new ObjectResponse("Success", entity, true);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), false);
        }
    }

    public ObjectResponse get(String id) {
        try {
            T entity = repository.findById(UUID.fromString(id)).get();
            return new ObjectResponse("Success", entity, true);
        } catch (Exception e) {
            return new ObjectResponse("Error", false);
        }
    }


}
