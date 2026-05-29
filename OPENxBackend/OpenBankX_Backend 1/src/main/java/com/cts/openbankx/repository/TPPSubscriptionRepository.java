package com.cts.openbankx.repository;

import com.cts.openbankx.model.TPPSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TPPSubscriptionRepository extends JpaRepository<TPPSubscription, Long> {
    List<TPPSubscription> findByTppApp_TppAppId(Long tppAppId);


    @Modifying
    @Query("DELETE FROM TPPSubscription s WHERE s.apiPlan.planId = :planId")
    int deleteAllByPlanId(@Param("planId") Long planId);


    @Modifying
    @Query("DELETE FROM TPPSubscription s WHERE s.apiPlan.apiProduct.productId = :productId")
    int deleteAllByProductId(@Param("productId") Long productId);
}