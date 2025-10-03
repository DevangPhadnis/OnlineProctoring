package com.example.OnlineProctoring.models;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionsDTO {

    private Long QuestionId;

    private QuestionType questionType;

    private String questionText;

    private Double defaultMarks;

    private Double defaultNegativeMarks;

    private String difficulty;

    private String subject;

    private String explanation;

    private boolean activeFlag;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<AnswerOptionDTO> answerOptionDTOList;

    public Long getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(Long questionId) {
        QuestionId = questionId;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Double getDefaultMarks() {
        return defaultMarks;
    }

    public void setDefaultMarks(Double defaultMarks) {
        this.defaultMarks = defaultMarks;
    }

    public Double getDefaultNegativeMarks() {
        return defaultNegativeMarks;
    }

    public void setDefaultNegativeMarks(Double defaultNegativeMarks) {
        this.defaultNegativeMarks = defaultNegativeMarks;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<AnswerOptionDTO> getAnswerOptionDTOList() {
        return answerOptionDTOList;
    }

    public void setAnswerOptionDTOList(List<AnswerOptionDTO> answerOptionDTOList) {
        this.answerOptionDTOList = answerOptionDTOList;
    }
}
