package com.api.bank.repository;

import com.api.bank.model.entity.Card;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository

public interface CardRepository extends GenericRepository<Card>{
   Card findCardByCardId(String cardId);
}
