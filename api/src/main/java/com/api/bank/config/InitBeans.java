package com.api.bank.config;

import com.api.bank.model.entity.Base;
import com.api.bank.repository.AccountRepository;
import com.api.bank.repository.ClientRepository;
import com.api.bank.repository.OperationRepository;
import com.api.bank.service.AccountService;
import com.api.bank.service.ClientService;
import com.api.bank.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.xml.sax.helpers.ParserFactory;

import java.util.UUID;

@Configuration
//@ComponentScan(basePackages = {"com.api.bank"})
//@RequiredArgsConstructor
@EnableAutoConfiguration
public class InitBeans {

    //    @Bean
//    public FactoryBean serviceLocatorFactoryBean() {
//        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
//        factoryBean.setServiceLocatorInterface(ParserFactory.class);
//        return factoryBean;
//    }



    @Bean
    public AccountService getAccountService() {
        return new AccountService();
    }

//
//    private OperationRepository operationRepository;
//    private ClientRepository clientRepository;


//    @Bean
//    public OperationService operationService() {
//        return new OperationService(operationRepository);
//    }
//
//    @Bean
//    public AccountService accountService() {
//        return new AccountService(accountRepository);
//    }
//
//    @Bean
//    public ClientService clientService() {
//        return new ClientService(clientRepository);
//    }

    ;
}

