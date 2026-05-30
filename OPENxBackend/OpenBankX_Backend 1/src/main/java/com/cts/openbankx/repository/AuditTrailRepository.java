package com.cts.openbankx.repository;

import com.cts.openbankx.model.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
    List<AuditTrail> findByActorId(String actorId);
    List<AuditTrail> findByResource(String resource);
}
