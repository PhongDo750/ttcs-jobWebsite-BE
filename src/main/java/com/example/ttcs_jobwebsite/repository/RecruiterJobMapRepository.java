package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.RecruiterJobMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruiterJobMapRepository extends JpaRepository<RecruiterJobMapEntity, Long> {
    Page<RecruiterJobMapEntity> findAllByRecruiterIdAndState(Long recruiterId, String state, Pageable pageable);

    Page<RecruiterJobMapEntity> findAllByRecruiterId(Long recruiterId, Pageable pageable);

    void deleteAllByRecruiterId(Long recruiterId);

    void deleteAllByUserJobIdIn(List<Long> userJobIds);
}
