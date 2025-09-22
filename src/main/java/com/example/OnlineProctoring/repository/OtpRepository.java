package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.OtpDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpDetails, Long> {

    Optional<OtpDetails> findTopByRecipientAndPurposeAndActiveFlagOrderByCreatedAtDesc(String recipient, String purpose, boolean activeFlag);

    @Transactional
    @Modifying
    @Query("update OtpDetails o set o.activeFlag = false where o.expiresAt < :now and o.activeFlag = true")
    int deleteExpired(@Param("now")Instant now);
}
