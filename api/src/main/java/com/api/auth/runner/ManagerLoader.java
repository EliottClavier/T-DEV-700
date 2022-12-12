package com.api.auth.runner;

import com.api.auth.entity.Manager;
import com.api.auth.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ManagerLoader implements ApplicationRunner {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${default.manager.username}")
    private String username;

    @Value("${default.manager.password}")
    private String password;

    public void run(ApplicationArguments args) {
        if (managerRepository.findByUsername(username).isEmpty()) {
            Manager manager = new Manager();
            manager.setUsername(username);
            manager.setPassword(passwordEncoder.encode(password));
            managerRepository.save(manager);
        }
    }
}
