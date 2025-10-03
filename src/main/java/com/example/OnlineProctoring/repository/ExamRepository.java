package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}
