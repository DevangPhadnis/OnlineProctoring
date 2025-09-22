package com.example.OnlineProctoring.service;

public interface OtpService {

    public String generateOtp(String recipient, String purpose, String requestIp, String userAgent) throws Exception;

    public String validateOtp(String recipient, String purpose, String providedOtp, String requestIp, String userAgent) throws Exception;
}
