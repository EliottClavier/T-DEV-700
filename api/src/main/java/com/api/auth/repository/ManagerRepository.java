package com.api.auth.repository;

import com.api.auth.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, String> {
    Optional<Manager> findByUsername(String username);
}
