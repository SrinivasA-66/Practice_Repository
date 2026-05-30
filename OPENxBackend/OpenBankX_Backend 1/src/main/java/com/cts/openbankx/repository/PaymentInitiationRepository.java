package com.cts.openbankx.repository;

import com.cts.openbankx.model.PaymentInitiation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentInitiationRepository extends JpaRepository<PaymentInitiation, Long> {
    List<PaymentInitiation> findByTppApp_TppAppId(Long tppAppId);
}
