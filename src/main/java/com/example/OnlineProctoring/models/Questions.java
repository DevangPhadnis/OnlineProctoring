package com.example.OnlineProctoring.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "question_details")
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long questionId;

    @Enumerated
    @Column(nullable = false)
    private QuestionType questionType;

    @Column(nullable = false)
    private String questionText;

    private Double defaultMarks = 1.0;

    private Double defaultNegativeMarks = 0.0;

    private String difficulty;

    private String subject;

    private String explanation;

    private boolean activeFlag = true;

    @OneToMany(mappedBy = "questions", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "question-option")
    private List<AnswerOption> answerOptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    @JsonBackReference(value = "exam-questions")
    private Exam exam;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public List<AnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
