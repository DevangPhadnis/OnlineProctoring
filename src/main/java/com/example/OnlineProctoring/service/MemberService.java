package com.example.OnlineProctoring.service;

import com.example.OnlineProctoring.models.MemberExamAttemptDTO;
import com.example.OnlineProctoring.models.MemberRegistrationDTO;

import java.util.List;

public interface MemberService {

    public Integer memberRegistration(MemberRegistrationDTO memberRegistrationDTO, String userName);

    public MemberExamAttemptDTO startExam(MemberExamAttemptDTO memberExamAttemptDTO, String userName);

    public List<MemberExamAttemptDTO> fetchExamQuestionList(MemberExamAttemptDTO memberExamAttemptDTO, Integer pageNumber, Integer pageSize);
}
