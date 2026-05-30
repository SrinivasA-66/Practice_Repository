package com.cts.openbankx.repository;

import com.cts.openbankx.model.SCAEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SCAEventRepository extends JpaRepository<SCAEvent, Long> {


    List<SCAEvent> findByReferenceId(String referenceId);

    List<SCAEvent> findByUser_UserId(Long userId);

    @Modifying
    @Query("DELETE FROM SCAEvent s WHERE s.user.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}