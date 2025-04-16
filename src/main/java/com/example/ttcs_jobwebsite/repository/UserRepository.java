package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    UserEntity findByUsername(String username);

    List<UserEntity> findAllByIdIn(Set<Long> userIds);

    UserEntity findByGoogleId(String googleId);

    UserEntity findByEmail(String email);

    @Query("select u from UserEntity u where u.role != :role")
    Page<UserEntity> findAllUserWithoutAdmin(String role, Pageable pageable);
}
