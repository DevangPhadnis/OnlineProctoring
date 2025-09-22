package com.example.OnlineProctoring.service;

public interface RateLimiterService {

    public void validateAllowed(String recipient, String requestIp);

    public void validateVerifyAllowed(String recipient, String requestIp);

    public void recordFailedAttempt(String recipient);

    public void recordSuccess(String recipient);

    public void block(String recipient);
}
