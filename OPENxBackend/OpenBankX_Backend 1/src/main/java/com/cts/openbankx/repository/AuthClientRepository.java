package com.cts.openbankx.repository;

import com.cts.openbankx.model.AuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuthClientRepository extends JpaRepository<AuthClient, Long> {
    List<AuthClient> findByTppApp_TppAppId(Long tppAppId);
}
