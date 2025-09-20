package com.example.OnlineProctoring.service;

public interface EmailService {

    public void sendEmailWithoutAttachment(String to, String subject, String body);
}
