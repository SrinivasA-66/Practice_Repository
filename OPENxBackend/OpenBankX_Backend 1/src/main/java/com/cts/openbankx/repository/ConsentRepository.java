package com.cts.openbankx.repository;

import com.cts.openbankx.model.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsentRepository extends JpaRepository<Consent, Long> {
    List<Consent> findByUser_UserId(Long userId);

    @Modifying
    @Query("DELETE FROM Consent c WHERE c.user.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}