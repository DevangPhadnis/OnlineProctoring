package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.customExceptions.RateLimitExceededException;
import com.example.OnlineProctoring.models.OtpDetails;
import com.example.OnlineProctoring.repository.OtpRepository;
import com.example.OnlineProctoring.service.OtpService;
import com.example.OnlineProctoring.service.RateLimiterService;
import com.example.OnlineProctoring.utils.OtpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OtpServiceImpl implements OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);

    private final OtpRepository otpRepository;
    private final RateLimiterService rateLimiterService;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.expiryMinutes}")
    private int expiryMinutes;

    @Value("${otp.maxAttempts}")
    private int maxAttempts;

    public OtpServiceImpl(OtpRepository otpRepository, RateLimiterService rateLimiterService) {
        this.otpRepository = otpRepository;
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public String generateOtp(String recipient, String purpose, String requestIp, String userAgent) throws Exception {
        logger.info("Inside GenerateOtp method of OtpServiceImpl");
        try {
            rateLimiterService.validateAllowed(recipient, requestIp);

            otpRepository.deactivateActiveForRecipientPurpose(recipient, purpose);

            String otp = OtpUtils.generateNumericOtp(otpLength);
            String salt = OtpUtils.generateSalt();
            String hash = OtpUtils.hashOtp(otp, salt);

            OtpDetails otpDetails = new OtpDetails();
            otpDetails.setRecipient(recipient);
            otpDetails.setPurpose(purpose);
            otpDetails.setSalt(salt);
            otpDetails.setOtpHash(hash);
            otpDetails.setCreatedAt(LocalDateTime.now());
            otpDetails.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
            otpDetails.setRequestIp(requestIp);
            otpDetails.setUserAgent(userAgent);
            otpDetails.setUsed(false);
            otpDetails.setActiveFlag(true);

            otpRepository.saveAndFlush(otpDetails);

            logger.info("Outside GenerateOtp method of OtpServiceImpl");
            return otp;
        } catch (RateLimitExceededException rateLimitExceededException) {
            logger.error("Error Found", rateLimitExceededException);
            return "2";
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String validateOtp(String recipient, String purpose, String providedOtp, String requestIp, String userAgent) throws Exception {
        try {
            rateLimiterService.validateVerifyAllowed(recipient, requestIp);

            Optional<OtpDetails> otpDetailsOptional = otpRepository.findTopByRecipientAndPurposeAndActiveFlagOrderByCreatedAtDesc(recipient, purpose, true);
            if(otpDetailsOptional.isEmpty()) {
                rateLimiterService.recordFailedAttempt(recipient);
                return "false";
            }
            OtpDetails otpDetails = otpDetailsOptional.get();

            if(otpDetails.isUsed()) {
                rateLimiterService.recordFailedAttempt(recipient);
                return "false";
            }
            if(otpDetails.getExpiresAt().isBefore(LocalDateTime.now())) {
                rateLimiterService.recordFailedAttempt(recipient);
                return "false";
            }
            if(otpDetails.getAttempts() >= maxAttempts) {
                rateLimiterService.block(recipient);
                return "maxAttemptsCompleted";
            }

            boolean isOtpVerified = OtpUtils.verifyOtp(providedOtp, otpDetails.getSalt(), otpDetails.getOtpHash());
            otpDetails.setAttempts(otpDetails.getAttempts() + 1);
            if(isOtpVerified) {
                otpDetails.setUsed(true);
                otpDetails.setActiveFlag(false);
                otpDetails.setVerifiedAt(LocalDateTime.now());
                otpRepository.save(otpDetails);
                otpRepository.deactivateOthers(recipient, purpose, otpDetails.getOtpId());
                rateLimiterService.recordSuccess(recipient);
                return "true";
            } else {
                otpDetails.setActiveFlag(true);
                otpRepository.save(otpDetails);
                rateLimiterService.recordFailedAttempt(recipient);
                return "false";
            }
        } catch (RateLimitExceededException rateLimitExceededException) {
            logger.error("Error Found", rateLimitExceededException);
            return "2";
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
