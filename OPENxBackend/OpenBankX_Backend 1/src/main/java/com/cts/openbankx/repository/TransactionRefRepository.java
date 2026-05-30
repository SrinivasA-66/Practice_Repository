package com.cts.openbankx.repository;

import com.cts.openbankx.model.TransactionRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRefRepository extends JpaRepository<TransactionRef, Long> {
    List<TransactionRef> findByAccountRef_AccountId(Long accountId);

    @Modifying
    @Query("DELETE FROM TransactionRef t WHERE t.accountRef.user.userId = :userId")
    int deleteAllByAccountUserId(@Param("userId") Long userId);
}
