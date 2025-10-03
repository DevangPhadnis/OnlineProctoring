package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepository extends JpaRepository<Questions, Long> {
}
