package com.api.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface GenericRepository<T> extends JpaRepository<T, UUID> {


}
