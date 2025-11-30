package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    Page<Exam> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveFlagOrderByStartDateDesc
            (LocalDateTime startDateTime, LocalDateTime endDateTime, boolean activeFlag, Pageable pageable);

    Page<Exam> findByStartDateAfterAndActiveFlagOrderByStartDateDesc
            (LocalDateTime startDateTime, boolean activeFlag, Pageable pageable);

    Optional<Exam> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveFlagAndExamId
            (LocalDateTime startDateTime, LocalDateTime endDateTime, boolean activeFlag, Long examId);
}
