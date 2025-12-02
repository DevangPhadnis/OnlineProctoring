package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.MemberExamQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberExamQuestionAnswerRepository extends JpaRepository<MemberExamQuestionAnswer, Long> {

    List<MemberExamQuestionAnswer> findByMemberExamAttemptAttemptIdAndMemberExamQuestionMemQuestionIdAndActiveFlag
            (Long attemptId, Long memberExamQuestionId, boolean activeFlag);
}
