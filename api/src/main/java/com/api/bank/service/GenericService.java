package com.api.bank.service;

import com.api.bank.model.entity.Base;
import com.api.bank.model.ObjectResponse;
import com.api.bank.model.exception.BankTransactionException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class GenericService<T extends Base, T1 extends JpaRepository<T, UUID>> {

    private T1 repository;

    public GenericService(T1 repository) {
        this.repository = repository;
    }
    public GenericService() {
        this.repository = null;
    }

    @Transactional()
    public ObjectResponse add(T entity) {
        try {
            var res = repository.save(entity);
            repository.flush();
            return new ObjectResponse("Success", res, true, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BankTransactionException.class)
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = BankTransactionException.class)
    public ObjectResponse update(T entity) {
        try {
            entity.setModifiedAt(Instant.now());
            repository.save(entity);
            repository.flush();
            return new ObjectResponse("Success", entity,true, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), false , HttpStatus.CONFLICT);
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
