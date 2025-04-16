package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {
    Page<JobEntity> findAllByUserId(Long userId, Pageable pageable);

    Page<JobEntity> findAllByIdIn(List<Long> jobIds, Pageable pageable);

    @Query("SELECT j FROM JobEntity j WHERE j.id IN :jobIds")
    List<JobEntity> findAllByIdInWithoutPaging(@Param("jobIds") List<Long> jobIds);

    @Query("SELECT COUNT(u) FROM JobEntity u " +
            "WHERE EXTRACT(YEAR FROM u.createAt) = :year " +
            "AND EXTRACT(MONTH FROM u.createAt) = :month")
    int countJobsPostedInMonth(@Param("year") int year, @Param("month") int month);


    @Query("SELECT u FROM JobEntity u " +
            "WHERE EXTRACT(YEAR FROM u.createAt) = :year " +
            "AND EXTRACT(MONTH FROM u.createAt) = :month")
    Page<JobEntity> findJobsPostedInMonth(@Param("year") int year, @Param("month") int month, Pageable pageable);

    @Query("SELECT j FROM JobEntity j WHERE j.userId = :userId")
    List<JobEntity> findAllByUserIdWithoutPaging(@Param("userId") Long userId);

    void deleteAllByUserId(Long userId);

}


