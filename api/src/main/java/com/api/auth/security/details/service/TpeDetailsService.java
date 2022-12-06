package com.api.auth.security.details.service;

import com.api.bank.model.entity.Tpe;
import com.api.bank.repository.TpeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class TpeDetailsService implements UserDetailsService {
    @Autowired
    private TpeRepository tpeRepository;

    @Override
    public UserDetails loadUserByUsername(String mac) throws UsernameNotFoundException, RuntimeException {
        Optional<Tpe> tpeRes = tpeRepository.findByMac(mac);
        if(tpeRes.isEmpty())
            throw new UsernameNotFoundException("Could not find TPE with mac = " + mac + ".");
        Tpe tpe = tpeRes.get();
        if (!tpe.getWhitelisted()) {
            throw new RuntimeException("TPE not whitelisted.");
        }
        return new org.springframework.security.core.userdetails.User(mac, tpe.getSerial(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TPE")));
    }
}
