package com.cts.openbankx.repository;

import com.cts.openbankx.model.APILog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface APILogRepository extends JpaRepository<APILog, Long> {

    List<APILog> findByTppApp_TppAppId(Long tppAppId);

    List<APILog> findByEndpoint(String endpoint);

    List<APILog> findByStatusCodeGreaterThanEqual(Integer statusCode);
}

