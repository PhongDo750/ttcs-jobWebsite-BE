package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.JobLikeMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLikeMapRepository extends JpaRepository<JobLikeMapEntity, Long> {
    void deleteByUserIdAndJobId(Long userId, Long jobId);

    List<JobLikeMapEntity> findAllByUserId(Long userId);

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    void deleteAllByUserId(Long userId);

    void deleteAllByJobIdIn(List<Long> jobIds);

    void deleteAllByJobId(Long jobId);
}
