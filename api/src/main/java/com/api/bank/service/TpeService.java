package com.api.bank.service;

import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import org.springframework.stereotype.Service;

@Service
public class TpeService extends GenericService<Tpe, TpeRepository> {

    public TpeRepository tpeRepository;

    public TpeService(TpeRepository tpeRepository) {
        super(tpeRepository);
    }

}

