package com.example.OnlineProctoring.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_exam_question_details")
public class MemberExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long memQuestionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private MemberExamAttempt memberExamAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Questions questions;

    private Integer sequenceNumber;

    private LocalDateTime attemptedTime;

    private LocalDateTime createdAt;

    private boolean activeFlag;

    public Long getMemQuestionId() {
        return memQuestionId;
    }

    public void setMemQuestionId(Long memQuestionId) {
        this.memQuestionId = memQuestionId;
    }

    public MemberExamAttempt getMemberExamAttempt() {
        return memberExamAttempt;
    }

    public void setMemberExamAttempt(MemberExamAttempt memberExamAttempt) {
        this.memberExamAttempt = memberExamAttempt;
    }

    public Questions getQuestions() {
        return questions;
    }

    public void setQuestions(Questions questions) {
        this.questions = questions;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    public LocalDateTime getAttemptedTime() {
        return attemptedTime;
    }

    public void setAttemptedTime(LocalDateTime attemptedTime) {
        this.attemptedTime = attemptedTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
}
