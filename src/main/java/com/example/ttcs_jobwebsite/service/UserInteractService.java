package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.cloudinary.CloudinaryHelper;
import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.job.ApplyJobInput;
import com.example.ttcs_jobwebsite.entity.*;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import com.example.ttcs_jobwebsite.repository.*;
import com.example.ttcs_jobwebsite.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class UserInteractService {
    private final UserJobMapRepository userJobMapRepository;
    private final CustomRepository customRepository;
    private final JobLikeMapRepository jobLikeMapRepository;
    private final RecruiterJobMapRepository recruiterJobMapRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public ApiResponse<?> applyJob(String accessToken, ApplyJobInput applyJobInput, MultipartFile filePDF) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (userEntity.getRole().equals(Common.RECRUITER)) {
            throw new AppException(ErrorCode.UN_AUTHORIZATION);
        }

        if (Objects.isNull(filePDF)) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }

        JobEntity jobEntity = customRepository.getJobBy(applyJobInput.getJobId());

        UserJobMapEntity userJobMapEntity = UserJobMapEntity.builder()
                .userId(userId)
                .jobId(applyJobInput.getJobId())
                .introduction(applyJobInput.getIntroductions())
                .cvUrl(CloudinaryHelper.uploadAndGetFileUrl(filePDF))
                .state(Common.APPLIED)
                .createAt(LocalDateTime.now())
                .build();
        userJobMapRepository.save(userJobMapEntity);

        RecruiterJobMapEntity recruiterJobMapEntity = RecruiterJobMapEntity.builder()
                .recruiterId(jobEntity.getUserId())
                .userJobId(userJobMapEntity.getId())
                .state(Common.PENDING_APPROVAL)
                .createAt(LocalDateTime.now())
                .build();
        recruiterJobMapRepository.save(recruiterJobMapEntity);

        CompletableFuture.runAsync(() -> {
            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(userId)
                            .interactId(jobEntity.getUserId())
                            .jobId(applyJobInput.getJobId())
                            .hasSeen(Boolean.FALSE)
                            .type(Common.APPLIED)
                            .createAt(LocalDateTime.now())
                            .build()
            );

            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(jobEntity.getUserId())
                            .interactId(userId)
                            .jobId(applyJobInput.getJobId())
                            .hasSeen(Boolean.FALSE)
                            .type(Common.APPLIED)
                            .createAt(LocalDateTime.now())
                            .build()
            );

//            String jsonMessage = "{\"title\": \"Thông báo mới\", \"body\": \"Một ứng viên vừa ứng tuyển vào công ty bạn\"}";
//            pushNotificationService.sendNotification(jobEntity.getUserId(), jsonMessage);
        });
        return ApiResponse.builder()
                .message("OK")
                .code(200)
                .build();
    }

    @Transactional
    public void likeJob(String accessToken, Long jobId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        JobLikeMapEntity jobLikeMapEntity = JobLikeMapEntity.builder()
                .userId(userId)
                .jobId(jobId)
                .createAt(LocalDateTime.now())
                .build();
        jobLikeMapRepository.save(jobLikeMapEntity);
    }

    @Transactional
    public void removeLikeJob(String accessToken, Long jobId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        jobLikeMapRepository.deleteByUserIdAndJobId(userId, jobId);
    }
}
