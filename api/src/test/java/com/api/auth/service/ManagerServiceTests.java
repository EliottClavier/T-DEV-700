package com.api.auth.service;

import com.api.auth.entity.Manager;
import com.api.auth.repository.ManagerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ManagerServiceTests {

    private final ManagerService managerService;
    private final ManagerRepository managerRepository;

    private final String username = "test";
    private final String password = "test";

    @Autowired
    public ManagerServiceTests(
            ManagerService managerService,
            ManagerRepository managerRepository
    ) {
        this.managerService = managerService;
        this.managerRepository = managerRepository;
    }

    @BeforeEach
    public void setup() {
        if (!managerRepository.existsByUsername(username)) {
            managerRepository.save(new Manager(username, password));
        }

        if (managerRepository.existsByUsername("test2")) {
            managerRepository.deleteByUsername("test2");
        }
    }

    @Test
    public void testRegisterManager() {
        Manager manager = new Manager("test2", "test2");
        // Assert that the manager does not exist
        assertFalse(managerRepository.existsByUsername(manager.getUsername()));

        // Register the manager
        managerService.registerManager(manager);

        // Assert that the manager exists
        assertTrue(managerRepository.existsByUsername(manager.getUsername()));
    }

    @Test
    public void testRegisterManagerBlocked() {
        Manager manager = new Manager(username, password);

        // Assert that the manager exists and registering it again will return null
        assertNull(managerService.registerManager(manager));
    }

}
