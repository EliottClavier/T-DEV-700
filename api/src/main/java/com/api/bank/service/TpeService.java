package com.api.bank.service;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class TpeService extends GenericService<Tpe> {

    public TpeRepository tpeRepository;

    @Autowired
    public TpeService(TpeRepository tpeRepository) {
        super(tpeRepository);
        this.tpeRepository = tpeRepository;
    }

    public ObjectResponse getTpeById(String id) {
        try {
            Tpe tpe = tpeRepository.findById(UUID.fromString(id)).get();
            return new ObjectResponse("TPE found.", tpe, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    public ObjectResponse updateTpeStatus(String id, Boolean whitelisted) {
        try {
            Tpe tpe = tpeRepository.findById(UUID.fromString(id)).get();
            if (tpe.getWhitelisted() != whitelisted) {
                tpe.setWhitelisted(whitelisted);
                tpeRepository.save(tpe);
                tpeRepository.flush();
                return new ObjectResponse("Success", tpe, HttpStatus.OK);
            } else {
                return new ObjectResponse(String.format("Tpe already %s", whitelisted ? "whitelisted" : "blacklisted"), HttpStatus.CONFLICT);
            }
        } catch (IllegalArgumentException e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}

