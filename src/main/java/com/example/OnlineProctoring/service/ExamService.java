package com.example.OnlineProctoring.service;

import com.example.OnlineProctoring.models.ExamDTO;

public interface ExamService {

    public Long createExam(ExamDTO examDTO, String userName) throws Exception;
}
