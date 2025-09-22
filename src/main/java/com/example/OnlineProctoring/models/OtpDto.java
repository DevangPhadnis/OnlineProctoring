package com.example.OnlineProctoring.models;

public class OtpDto {

    private String userName;
    private String providedOtp;
    private String createdDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProvidedOtp() {
        return providedOtp;
    }

    public void setProvidedOtp(String providedOtp) {
        this.providedOtp = providedOtp;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
