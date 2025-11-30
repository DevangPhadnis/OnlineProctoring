package com.example.OnlineProctoring.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_attempt_details")
public class MemberExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_registration_id")
    private MemberRegistration memberRegistration;

    private LocalDateTime startedAt;

    private LocalDateTime endsAt;

    private LocalDateTime submittedAt;

    private LocalDateTime createdAt;

    private String attemptStatus;

    private boolean activeFlag;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public MemberRegistration getMemberRegistration() {
        return memberRegistration;
    }

    public void setMemberRegistration(MemberRegistration memberRegistration) {
        this.memberRegistration = memberRegistration;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAttemptStatus() {
        return attemptStatus;
    }

    public void setAttemptStatus(String attemptStatus) {
        this.attemptStatus = attemptStatus;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
}
