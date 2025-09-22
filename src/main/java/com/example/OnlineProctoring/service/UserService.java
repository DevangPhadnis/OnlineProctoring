package com.example.OnlineProctoring.service;

import com.example.OnlineProctoring.models.OtpDto;
import com.example.OnlineProctoring.models.UserAuth;
import com.example.OnlineProctoring.models.UserDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    public Long addNewUser(UserDTO userDTO) throws Exception;

    public String verifyUser(UserAuth userAuth, HttpServletRequest request) throws Exception;

    public Long sendOtpPassword(OtpDto otpDto, HttpServletRequest request) throws Exception;

    public Long verifyOtpPassword(OtpDto otpDto, HttpServletRequest request) throws Exception;
}
