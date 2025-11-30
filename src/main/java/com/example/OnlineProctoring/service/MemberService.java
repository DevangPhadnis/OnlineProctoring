package com.example.OnlineProctoring.service;

import com.example.OnlineProctoring.models.MemberRegistrationDTO;

public interface MemberService {

    public Integer memberRegistration(MemberRegistrationDTO memberRegistrationDTO, String userName);
}
