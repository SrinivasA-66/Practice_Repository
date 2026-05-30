package com.cts.openbankx.repository;

import com.cts.openbankx.model.AccountRef;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AccountRefRepository extends JpaRepository<AccountRef, Long> {
    List<AccountRef> findByUser_UserId(Long userId);
    Optional<AccountRef> findByAccountNumberMasked(String accountNumberMasked);

    @Modifying
    @Query("DELETE FROM AccountRef a WHERE a.user.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}