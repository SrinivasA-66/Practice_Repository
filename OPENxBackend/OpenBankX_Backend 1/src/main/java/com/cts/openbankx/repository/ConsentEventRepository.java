package com.cts.openbankx.repository;

import com.cts.openbankx.model.ConsentEvent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsentEventRepository extends JpaRepository<ConsentEvent, Long> {
	List<ConsentEvent> findByConsent_ConsentId(Long consentId);

	@Modifying
	@Query("DELETE FROM ConsentEvent e WHERE e.consent.user.userId = :userId")
	int deleteAllByConsentUserId(@Param("userId") Long userId);
}