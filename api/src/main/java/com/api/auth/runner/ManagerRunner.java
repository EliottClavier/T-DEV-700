package com.api.auth.runner;

import com.api.auth.entity.Manager;
import com.api.auth.repository.ManagerRepository;
import com.api.auth.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ManagerRunner implements ApplicationRunner {

    @Autowired
    private ManagerService managerService;

    @Value("${default.manager.username}")
    private String username;

    @Value("${default.manager.password}")
    private String password;

    public void run(ApplicationArguments args) {
        Manager manager = new Manager(username, password);
        managerService.registerManager(manager);
    }
}
