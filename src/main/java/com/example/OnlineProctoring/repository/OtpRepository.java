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

    @Modifying
    @Query("update OtpDetails o set o.activeFlag = false where o.recipient = :recipient and o.purpose = :purpose and o.activeFlag = true")
    int deactivateActiveForRecipientPurpose(@Param("recipient") String recipient,
                                            @Param("purpose") String purpose);

    // deactivate other actives except one (used after success)
    @Modifying
    @Query("update OtpDetails o set o.activeFlag = false where o.recipient = :recipient and o.purpose = :purpose and o.id <> :otpId and o.activeFlag = true")
    int deactivateOthers(@Param("recipient") String recipient,
                         @Param("purpose") String purpose,
                         @Param("otpId") Long otpId);
}
