package com.cts.openbankx.repository;

import com.cts.openbankx.model.FundsCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FundsCheckRepository extends JpaRepository<FundsCheck, Long> {
    List<FundsCheck> findByTppApp_TppAppId(Long tppAppId);
    List<FundsCheck> findByAccountRef_AccountId(Long accountId);

    @Modifying
    @Query("DELETE FROM FundsCheck f WHERE f.accountRef.user.userId = :userId")
    int deleteAllByAccountUserId(@Param("userId") Long userId);
}
