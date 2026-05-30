package com.cts.openbankx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cts.openbankx.model.APIPlan;

@Repository
public interface APIPlanRepository extends JpaRepository<APIPlan, Long> {


    List<APIPlan> findByApiProduct_ProductId(Long productId);

    /** Wipe all plans of a product — used when an admin deletes the product. */
    @Modifying
    @Query("DELETE FROM APIPlan p WHERE p.apiProduct.productId = :productId")
    int deleteAllByProductId(@Param("productId") Long productId);
}