package com.api.bank.repository;

import com.api.bank.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IReportRepository extends JpaRepository<Report, UUID> {


}
