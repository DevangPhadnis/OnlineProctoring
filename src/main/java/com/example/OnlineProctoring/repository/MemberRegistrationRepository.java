package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.MemberRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRegistrationRepository extends JpaRepository<MemberRegistration, Long> {

    MemberRegistration findByExamExamIdAndUserIdAndActiveFlag(Long examId, Long userId, boolean activeFlag);
}
