package com.example.OnlineProctoring.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_exam_question_ans_details")
public class MemberExamQuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long memberExamQuesAnsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_question_id")
    private MemberExamQuestion memberExamQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optionId")
    private AnswerOption answerOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private MemberExamAttempt memberExamAttempt;

    private boolean isCorrect;

    private LocalDateTime createdAt;

    private boolean activeFlag;

    public Long getMemberExamQuesAnsId() {
        return memberExamQuesAnsId;
    }

    public void setMemberExamQuesAnsId(Long memberExamQuesAnsId) {
        this.memberExamQuesAnsId = memberExamQuesAnsId;
    }

    public MemberExamQuestion getMemberExamQuestion() {
        return memberExamQuestion;
    }

    public void setMemberExamQuestion(MemberExamQuestion memberExamQuestion) {
        this.memberExamQuestion = memberExamQuestion;
    }

    public AnswerOption getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(AnswerOption answerOption) {
        this.answerOption = answerOption;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
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

    public MemberExamAttempt getMemberExamAttempt() {
        return memberExamAttempt;
    }

    public void setMemberExamAttempt(MemberExamAttempt memberExamAttempt) {
        this.memberExamAttempt = memberExamAttempt;
    }
}
