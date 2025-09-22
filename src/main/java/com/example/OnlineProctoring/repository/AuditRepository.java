package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.PasswordResetAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<PasswordResetAudit, Long> {
}
