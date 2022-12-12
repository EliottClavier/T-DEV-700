package com.api.bank.service;

import com.api.bank.model.entity.Base;
import com.api.bank.model.ObjectResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
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
            T newEntity = repository.save(data);
            repository.flush();
            return new ObjectResponse("Success", newEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public ObjectResponse delete(T entity) {
        try {
            repository.delete(entity);
            repository.flush();
            return new ObjectResponse("Success", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public ObjectResponse deleteByUUID(String id) {
        try {
            repository.deleteById(UUID.fromString(id));
            repository.flush();
            return new ObjectResponse("Success", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public ObjectResponse update(T entity) {
        try {
            T originEntity = repository.findById(entity.getId()).get(); // get the entity from the database
            repository.save(originEntity);
            repository.flush();
            return new ObjectResponse("Success", entity, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public ObjectResponse getAll(){
        try {
            List<T> entities = repository.findAll();
            return new ObjectResponse("Success", entities, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ObjectResponse("Error", HttpStatus.CONFLICT);
        }
    }
    public ObjectResponse get(String id){
        try {
            T entity = repository.findById(UUID.fromString(id)).get();
            return new ObjectResponse("Success", entity, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
