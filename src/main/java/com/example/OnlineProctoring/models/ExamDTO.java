package com.example.OnlineProctoring.models;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ExamDTO {

    private Long examId;

    private String title;

    private String description;

    private String durationInSeconds;

    private String status;

    private boolean activeFlag;

    private boolean shuffleQuestions;

    private boolean negativeMarkingAllowed;

    private String subject;

    private Integer numberOfQuestion;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private List<QuestionsDTO> questionsList;

    private List<AnswerOptionDTO> optionList;

    private String attachmentUrl;

    private MultipartFile questionAnswerAttachment;

    private Long totalRecords;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(String durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public boolean isShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public boolean isNegativeMarkingAllowed() {
        return negativeMarkingAllowed;
    }

    public void setNegativeMarkingAllowed(boolean negativeMarkingAllowed) {
        this.negativeMarkingAllowed = negativeMarkingAllowed;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public void setNumberOfQuestion(Integer numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public List<QuestionsDTO> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(List<QuestionsDTO> questionsList) {
        this.questionsList = questionsList;
    }

    public List<AnswerOptionDTO> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<AnswerOptionDTO> optionList) {
        this.optionList = optionList;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public MultipartFile getQuestionAnswerAttachment() {
        return questionAnswerAttachment;
    }

    public void setQuestionAnswerAttachment(MultipartFile questionAnswerAttachment) {
        this.questionAnswerAttachment = questionAnswerAttachment;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
