package com.api.auth.security.details.service;

import com.api.auth.entity.Manager;
import com.api.auth.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class ManagerDetailsService implements UserDetailsService {
    @Autowired
    private ManagerRepository managerRepository;

    // Process to check if manager exists in database and give it the role of manager
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Manager> managerRes = managerRepository.findByUsername(username);
        if(managerRes.isEmpty())
            throw new UsernameNotFoundException("Could not findUser with username = " + username + ".");
        Manager manager = managerRes.get();
        return new org.springframework.security.core.userdetails.User(manager.getUsername(), manager.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER")));
    }
}