package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.notification.NotificationDTO;
import com.example.ttcs_jobwebsite.entity.JobEntity;
import com.example.ttcs_jobwebsite.entity.NotificationEntity;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import com.example.ttcs_jobwebsite.repository.JobRepository;
import com.example.ttcs_jobwebsite.repository.NotificationRepository;
import com.example.ttcs_jobwebsite.repository.UserRepository;
import com.example.ttcs_jobwebsite.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Transactional
    public ApiResponse<Page<NotificationDTO>> getNotifications(Pageable pageable, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<NotificationEntity> notificationEntities = notificationRepository.findAllByUserId(userId, pageable);

        List<NotificationEntity> notifications = notificationEntities.stream().filter(
                notificationEntity -> notificationEntity.getHasSeen().equals(Boolean.FALSE)
        ).collect(Collectors.toList());

        for (NotificationEntity notification : notifications) {
            System.out.println(notification.getId());
            notification.setHasSeen(Boolean.TRUE);
            notificationRepository.save(notification);
        }

        Map<Long, UserEntity> userEntityMap =  userRepository.findAllByIdIn(
                notificationEntities.stream().map(NotificationEntity::getInteractId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        List<Long> jodIds = notificationEntities.stream().map(NotificationEntity::getJobId).collect(Collectors.toList());

        Map<Long, JobEntity> jobEntityMap = jobRepository.findAllByIdInWithoutPaging(jodIds)
                .stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));

        return ApiResponse.<Page<NotificationDTO>>builder()
                .message("OK")
                .code(200)
                .data(
                        notificationEntities.map(
                                notificationEntity -> {
                                    UserEntity userEntity = userEntityMap.get(notificationEntity.getInteractId());
                                    JobEntity jobEntity = jobEntityMap.get(notificationEntity.getJobId());

                                    return NotificationDTO.builder()
                                            .id(notificationEntity.getId())
                                            .interactId(notificationEntity.getInteractId())
                                            .jobId(notificationEntity.getJobId())
                                            .jobName(jobEntity.getJobName())
                                            .fullName(userEntity.getFullName())
                                            .type(notificationEntity.getType())
                                            .hasSeen(notificationEntity.getHasSeen())
                                            .build();
                                }
                        )
                )
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<Integer> countNotificationsHasNotSeen(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        return ApiResponse.<Integer>builder()
                .message("OK")
                .code(200)
                .data(notificationRepository.countAllByUserIdAndHasSeenIsFalse(userId))
                .build();
    }
}
