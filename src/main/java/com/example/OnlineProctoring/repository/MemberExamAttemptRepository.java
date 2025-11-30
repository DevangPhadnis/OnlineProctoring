package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.MemberExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberExamAttemptRepository extends JpaRepository<MemberExamAttempt, Long> {

    Optional<MemberExamAttempt>
    findByExamExamIdAndMemberRegistrationMemberRegistrationIdAndActiveFlag
            (Long examId, Long memberRegistrationId, boolean activeFlag);
}
