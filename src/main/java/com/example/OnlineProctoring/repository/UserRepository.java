package com.example.OnlineProctoring.repository;

import com.example.OnlineProctoring.models.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserAuth, Long> {
    UserAuth findByUserName(String username);

    UserAuth findByEmail(String email);
}