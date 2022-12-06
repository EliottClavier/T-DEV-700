package com.api.bank.repository;

import com.api.bank.model.entity.Report;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ReportRepository extends GenericRepository<Report> {

}

