package com.example.OnlineProctoring.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long usrDetailsId;

    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String mobileNumber;
    private String addressOne;
    private String addressTwo;
    private String addressThree;
    private LocalDateTime createdDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    @JsonBackReference(value = "userAuth-details")
    private UserAuth userAuth;

    public Long getUsrDetailsId() {
        return usrDetailsId;
    }

    public void setUsrDetailsId(Long usrDetailsId) {
        this.usrDetailsId = usrDetailsId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserAuth getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(UserAuth userAuth) {
        this.userAuth = userAuth;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
    }

    public String getAddressThree() {
        return addressThree;
    }

    public void setAddressThree(String addressThree) {
        this.addressThree = addressThree;
    }
}
