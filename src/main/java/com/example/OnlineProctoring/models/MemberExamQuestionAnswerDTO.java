package com.example.OnlineProctoring.models;

import java.time.LocalDateTime;

public class MemberExamQuestionAnswerDTO {

    private Long attemptId;

    private Long memQuestionId;

    private Long memberExamQuesAnsId;

    private Long selectedOptionId;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getMemQuestionId() {
        return memQuestionId;
    }

    public void setMemQuestionId(Long memQuestionId) {
        this.memQuestionId = memQuestionId;
    }

    public Long getMemberExamQuesAnsId() {
        return memberExamQuesAnsId;
    }

    public void setMemberExamQuesAnsId(Long memberExamQuesAnsId) {
        this.memberExamQuesAnsId = memberExamQuesAnsId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
