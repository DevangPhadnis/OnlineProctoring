package com.example.OnlineProctoring.service;

import com.example.OnlineProctoring.models.ExamDTO;

import java.util.List;

public interface ExamService {

    public void createExam(ExamDTO examDTO, String userName) throws Exception;

    public List<ExamDTO> fetchOngoingExams(Integer pageNumber, Integer pageSize);

    public List<ExamDTO> fetchUpcomingExams(Integer pageNumber, Integer pageSize);
}
