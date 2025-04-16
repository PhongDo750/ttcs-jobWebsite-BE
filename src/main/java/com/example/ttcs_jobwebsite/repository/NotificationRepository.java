package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    @Query("SELECT n from NotificationEntity n where n.userId = :userId ORDER BY n.createAt DESC")
    Page<NotificationEntity> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    int countAllByUserIdAndHasSeenIsFalse(Long userId);

    void deleteAllByUserIdOrInteractId(Long userId, Long interactId);

    void deleteAllByJobId(Long jobId);
}
