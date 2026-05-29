package com.cts.openbankx.repository;

import com.cts.openbankx.model.RateLimitCounter;
import com.cts.openbankx.enums.LimitWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface RateLimitCounterRepository extends JpaRepository<RateLimitCounter, Long> {
    List<RateLimitCounter> findByTppApp_TppAppId(Long tppAppId);
    Optional<RateLimitCounter> findByTppApp_TppAppIdAndLimitWindowAndPeriodStart(
            Long tppAppId, LimitWindow limitWindow, LocalDateTime periodStart);
}
