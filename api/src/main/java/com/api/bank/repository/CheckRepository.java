package com.api.bank.repository;

import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.QrCheck;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckRepository extends GenericRepository<QrCheck>{

    QrCheck findQrCheckByCheckToken(String checkToken);
}
