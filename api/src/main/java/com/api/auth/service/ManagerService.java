package com.api.auth.service;

import com.api.auth.entity.Manager;
import com.api.auth.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ManagerService(
            ManagerRepository managerRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new manager
     *
     * @param manager Manager to create
     * @return Manager, or null if the manager already exists
     */
    public Manager registerManager(Manager manager) {
        if (!managerRepository.existsByUsername(manager.getUsername())) {
            manager.setPassword(passwordEncoder.encode(manager.getPassword()));
            return managerRepository.save(manager);
        }
        return null;
    }

}
