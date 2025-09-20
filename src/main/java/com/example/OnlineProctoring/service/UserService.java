package com.example.OnlineProctoring.service;

import com.example.OnlineProctoring.models.UserAuth;
import com.example.OnlineProctoring.models.UserDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    public Long addNewUser(UserDTO userDTO) throws Exception;

    public String verifyUser(UserAuth userAuth, HttpServletRequest request) throws Exception;
}
