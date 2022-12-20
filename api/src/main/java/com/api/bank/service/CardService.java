package com.api.bank.service;

import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.CardRepository;
import com.api.bank.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public class CardService extends GenericService<Card> {

    @Autowired
    public CardService(CardRepository cardRepository) {
        super(cardRepository);
    }

    public Card getCardByCardId(String cardId) {
        return ((CardRepository)repository).findCardByCardId(cardId);
    }
}


