package com.api.bank.service;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.Base;
import com.api.bank.model.ObjectResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

public class BaseService<T extends Base, T1 extends JpaRepository<T, UUID>> {
    private T1 repository;
    private T entity;
    @Autowired
    private ObjectMapper objectMapper;

    public ObjectResponse add(T data) {
        try {

            System.out.println("add");
            repository.save(entity);
            repository.flush();

            return new ObjectResponse("Success", entity, true);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), false);
        }
    }

    public ObjectResponse remove(T entity) {
        try {
            System.out.println("remove");

            repository.delete(entity);
            repository.flush();
            return new ObjectResponse("Success", true);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), false);
        }
    }

    public ObjectResponse update(T entity) {
        try {
            System.out.println("update");
            repository.save(entity);
            repository.flush();
            return new ObjectResponse("Success", entity, true);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), false);
        }
    }

    public ObjectResponse get(String id) {
        try {
            System.out.println("get");
            T entity = repository.findById(UUID.fromString(id)).get();
            return new ObjectResponse("Success", entity, true);
        } catch (Exception e) {
            return new ObjectResponse("Error", false);
        }
    }


}
