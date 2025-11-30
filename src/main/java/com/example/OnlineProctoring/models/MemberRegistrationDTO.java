package com.example.OnlineProctoring.models;

import java.time.LocalDateTime;

public class MemberRegistrationDTO {

    private Long memberRegistrationId;

    private Long examId;

    private Long paymentAmountPaid;

    private Long gstAmount;

    private Long paymentAmountInclusiveGst;

    private Long coupounId;

    private LocalDateTime createdAt;

    public Long getMemberRegistrationId() {
        return memberRegistrationId;
    }

    public void setMemberRegistrationId(Long memberRegistrationId) {
        this.memberRegistrationId = memberRegistrationId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getPaymentAmountPaid() {
        return paymentAmountPaid;
    }

    public void setPaymentAmountPaid(Long paymentAmountPaid) {
        this.paymentAmountPaid = paymentAmountPaid;
    }

    public Long getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(Long gstAmount) {
        this.gstAmount = gstAmount;
    }

    public Long getPaymentAmountInclusiveGst() {
        return paymentAmountInclusiveGst;
    }

    public void setPaymentAmountInclusiveGst(Long paymentAmountInclusiveGst) {
        this.paymentAmountInclusiveGst = paymentAmountInclusiveGst;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCoupounId() {
        return coupounId;
    }

    public void setCoupounId(Long coupounId) {
        this.coupounId = coupounId;
    }
}
