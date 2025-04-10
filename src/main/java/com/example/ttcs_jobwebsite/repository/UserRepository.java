package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    UserEntity findByUsername(String username);

    UserEntity findByGoogleId(String googleId);
}
