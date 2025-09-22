package com.example.OnlineProctoring.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_audit")
public class PasswordResetAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long auditId;

    @Column(nullable = false)
    private String username;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "client_ip", nullable = false, length = 100)
    private String clientIp;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(nullable = false, length = 50)
    private String event;

    @Column(length = 1000)
    private String details;

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
