package com.example.OnlineProctoring.models;

import java.time.LocalDateTime;

public class AnswerOptionDTO {

    private Long optionId;

    private String optionDetails;

    private boolean isCorrect = true;

    private Integer displayOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean activeFlag;

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getOptionDetails() {
        return optionDetails;
    }

    public void setOptionDetails(String optionDetails) {
        this.optionDetails = optionDetails;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        this.isCorrect = correct;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
}
