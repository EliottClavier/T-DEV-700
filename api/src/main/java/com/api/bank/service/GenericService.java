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

    public ObjectResponse add(T data) {
        try {
            repository.save(data);
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
            UUID uuid = UUID.fromString(id);
            repository.deleteById(uuid);
            repository.flush();
            return new ObjectResponse("Success");
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage());
        }
    }

    public ObjectResponse update(T entity) {
        try {
            T originEntity = repository.findById(entity.getId()).get(); // get the entity from the database
            repository.save(originEntity);
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
    public ObjectResponse get(UUID id){
        try {
            T entity = repository.findById(id).get();
            return new ObjectResponse("Success", entity);
        } catch (Exception e) {
            return new ObjectResponse("Error");
        }
    }
}