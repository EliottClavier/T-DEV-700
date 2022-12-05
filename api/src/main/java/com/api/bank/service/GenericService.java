package com.api.bank.service;

import com.api.bank.model.entity.Base;
import com.api.bank.model.ObjectResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GenericService<T extends Base, T1 extends JpaRepository<T, UUID>> {

    protected T entity;

    protected T1 repository;

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
            return new ObjectResponse("Success", entity);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage());
        }
    }

    public ObjectResponse delete(T entity) {
        try {
            repository.delete(entity);
            repository.flush();
            return new ObjectResponse("Success");
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage());
        }
    }

    public ObjectResponse deleteByUUID(String id) {
        try {
            repository.deleteById(UUID.fromString(id));
            repository.flush();
            return new ObjectResponse("Success");
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage());
        }
    }

    public ObjectResponse update(T entity) {
        try {
            repository.save(entity);
            repository.flush();
            return new ObjectResponse("Success", entity);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage());
        }
    }

    public ObjectResponse getAll(){
        try {
            List<T> entities = repository.findAll();
            return new ObjectResponse("Success", entities);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ObjectResponse("Error");
        }
    }
    public ObjectResponse get(String id){
        try {
            T entity = repository.findById(UUID.fromString(id)).get();
            return new ObjectResponse("Success", entity);
        } catch (Exception e) {
            return new ObjectResponse("Error");
        }
    }
}
