package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.MemberExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberExamQuestionRepository extends JpaRepository<MemberExamQuestion, Long> {

}
