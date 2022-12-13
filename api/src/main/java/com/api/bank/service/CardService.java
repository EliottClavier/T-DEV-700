package com.api.bank.service;

import com.api.bank.model.entity.Card;
import com.api.bank.model.entity.Client;
import com.api.bank.repository.CardRepository;
import com.api.bank.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;

public class CardService extends GenericService<Card, CardRepository> {

    public CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        super(cardRepository);
        this.cardRepository = cardRepository;
    }

    public Card getCardByCardId(String cardId) {
        return this.cardRepository.findCardByCardId(cardId);
    }
}


