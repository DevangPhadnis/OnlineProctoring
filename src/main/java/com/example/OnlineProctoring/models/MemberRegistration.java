package com.example.OnlineProctoring.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_details")
public class MemberRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long memberRegistrationId;

    private Long userId;

    private Long paymentAmountPaid;

    private Long gstAmount;

    private Long paymentAmountInclusiveGst;

    private Long coupounId;

    private Long transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    @JsonBackReference(value = "exam-members")
    private Exam exam;

    public Long getMemberRegistrationId() {
        return memberRegistrationId;
    }

    public void setMemberRegistrationId(Long memberRegistrationId) {
        this.memberRegistrationId = memberRegistrationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getCoupounId() {
        return coupounId;
    }

    public void setCoupounId(Long coupounId) {
        this.coupounId = coupounId;
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

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
