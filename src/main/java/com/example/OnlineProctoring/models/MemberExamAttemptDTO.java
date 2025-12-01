package com.example.OnlineProctoring.models;

import java.util.List;

public class MemberExamAttemptDTO {

    private Long attemptId;

    private Long examId;

    private Long questionId;

    private String questionName;

    private List<AnswerOptionDTO> answerOptionDTOList;

    private Long totalElements;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public List<AnswerOptionDTO> getAnswerOptionDTOList() {
        return answerOptionDTOList;
    }

    public void setAnswerOptionDTOList(List<AnswerOptionDTO> answerOptionDTOList) {
        this.answerOptionDTOList = answerOptionDTOList;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }
}
