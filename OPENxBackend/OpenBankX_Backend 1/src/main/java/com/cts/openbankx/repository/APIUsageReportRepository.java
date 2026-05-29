package com.cts.openbankx.repository;

import com.cts.openbankx.model.APIUsageReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface APIUsageReportRepository
        extends JpaRepository<APIUsageReport, Long> {

    Optional<APIUsageReport>
    findTopByScopeOrderByGeneratedDateDesc(String scope);
}