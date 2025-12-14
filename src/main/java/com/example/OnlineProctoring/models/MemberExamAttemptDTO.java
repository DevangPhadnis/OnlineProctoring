package com.example.OnlineProctoring.models;

import java.util.List;

public class MemberExamAttemptDTO {

    private Long attemptId;

    private Long examId;

    private Long questionId;

    private String questionName;

    private List<MemberExamAttemptOptionDTO> answerOptionDTOList;

    private Long totalElements;

    private QuestionType questionType;

    private List<MemberExamQuestionAnswerDTO> memberExamQuestionAnswerList;

    private Long memberExamQuestionId;

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

    public List<MemberExamAttemptOptionDTO> getAnswerOptionDTOList() {
        return answerOptionDTOList;
    }

    public void setAnswerOptionDTOList(List<MemberExamAttemptOptionDTO> answerOptionDTOList) {
        this.answerOptionDTOList = answerOptionDTOList;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<MemberExamQuestionAnswerDTO> getMemberExamQuestionAnswerList() {
        return memberExamQuestionAnswerList;
    }

    public void setMemberExamQuestionAnswerList(List<MemberExamQuestionAnswerDTO> memberExamQuestionAnswerList) {
        this.memberExamQuestionAnswerList = memberExamQuestionAnswerList;
    }

    public Long getMemberExamQuestionId() {
        return memberExamQuestionId;
    }

    public void setMemberExamQuestionId(Long memberExamQuestionId) {
        this.memberExamQuestionId = memberExamQuestionId;
    }
}
