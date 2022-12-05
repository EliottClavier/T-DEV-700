package com.api.bank.service;

import com.api.bank.model.ObjectResponse;
import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TpeService extends GenericService<Tpe, TpeRepository> {

    @Autowired
    public TpeRepository tpeRepository;

    public TpeService(TpeRepository tpeRepository) {
        super(tpeRepository);
    }

    public ObjectResponse updateTpeStatus(String id, Boolean whitelisted) {
        try {
            Tpe tpe = tpeRepository.findById(UUID.fromString(id)).get();
            tpe.setWhitelisted(whitelisted);
            tpeRepository.save(tpe);
            tpeRepository.flush();
            return new ObjectResponse("Success", tpe, HttpStatus.OK);
        } catch (Exception e) {
            return new ObjectResponse(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

}

