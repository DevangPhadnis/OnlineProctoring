package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.MemberExamQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberExamQuestionRepository extends JpaRepository<MemberExamQuestion, Long> {

    Page<MemberExamQuestion> findByMemberExamAttemptAttemptIdAndActiveFlag(Long attemptId, boolean activeFlag, Pageable pageable);
}
