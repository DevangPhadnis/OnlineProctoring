package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.customExceptions.RateLimitExceededException;
import com.example.OnlineProctoring.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    @Value("${redis.maxSenderHour}")
    private int maxSendPerHour;

    @Value("${redis.maxVerifyAttempts}")
    private int maxVerifyAttempts;

    private final Duration sendWindow = Duration.ofHours(1);
    private final Duration verifyWindow = Duration.ofMinutes(10);
    private final Duration blockWindow = Duration.ofMinutes(30);

    public RateLimiterServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void validateAllowed(String recipient, String requestIp) {
        String key = "otp:send:" + recipient;
        Long count = redisTemplate.opsForValue().increment(key);
        if(count != null && count == 1L) {
            redisTemplate.expire(key, sendWindow);
        }
        if(count != null && count > maxSendPerHour) {
            throw new RateLimitExceededException("Too Many OTP Requests");
        }
    }

    @Override
    public void validateVerifyAllowed(String recipient, String requestIp) {
        String blockKey = "otp:block:" + recipient;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(blockKey))) {
            throw new RuntimeException("User temporarily blocked");
        }
    }

    @Override
    public void recordFailedAttempt(String recipient) {
        String key = "otp:verify:" + recipient;
        Long count = redisTemplate.opsForValue().increment(key);
        if(count != null && count == 1L) {
            redisTemplate.expire(key, sendWindow);
        }
        if(count != null && count >= maxVerifyAttempts) {
            String blockKey = "otp:block:" + recipient;
            redisTemplate.opsForValue().set(blockKey, "1", blockWindow);
        }
    }

    @Override
    public void recordSuccess(String recipient) {
        redisTemplate.delete("otp:verify:" + recipient);
    }

    @Override
    public void block(String recipient) {
        String blockKey = "otp:block:" + recipient;
        redisTemplate.opsForValue().set(blockKey,"1", blockWindow);
    }
}
