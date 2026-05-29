package com.cts.openbankx.repository;

import com.cts.openbankx.model.TPPApp;
import com.cts.openbankx.enums.AppStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TPPAppRepository extends JpaRepository<TPPApp, Long> {
    List<TPPApp> findByTpp_TppId(Long tppId);
    List<TPPApp> findByStatus(AppStatus status);
}
