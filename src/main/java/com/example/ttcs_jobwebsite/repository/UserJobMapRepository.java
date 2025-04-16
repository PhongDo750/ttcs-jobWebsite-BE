package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.UserJobMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserJobMapRepository extends JpaRepository<UserJobMapEntity, Long> {
    Page<UserJobMapEntity> findAllByUserIdAndState(Long userId, String state, Pageable pageable);

    Page<UserJobMapEntity> findAllByUserId(Long userId, Pageable pageable);

    List<UserJobMapEntity> findAllByIdIn(List<Long> userJobIds);

    @Query("SELECT COUNT(u) FROM UserJobMapEntity u " +
            "WHERE EXTRACT(YEAR FROM u.createAt) = :year " +
            "AND EXTRACT(MONTH FROM u.createAt) = :month")
    int countUsersAppliedInMonth(@Param("year") int year, @Param("month") int month);

    void deleteAllByJobIdIn(List<Long> jobIds);

    void  deleteAllByJobId(Long jobId);

    void deleteAllByUserId(Long userId);

    List<UserJobMapEntity> findAllByJobId(Long jobId);

}
