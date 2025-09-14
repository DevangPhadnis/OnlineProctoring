package com.example.OnlineProctoring.service;

import jakarta.servlet.http.HttpServletRequest;

public interface OAuthService {

    public String oAuthLogin(String tokenId, HttpServletRequest request);
}
