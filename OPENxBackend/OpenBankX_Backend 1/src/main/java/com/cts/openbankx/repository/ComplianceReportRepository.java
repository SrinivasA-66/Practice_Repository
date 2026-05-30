package com.cts.openbankx.repository;

import com.cts.openbankx.model.ComplianceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplianceReportRepository extends JpaRepository<ComplianceReport, Long> {
}
