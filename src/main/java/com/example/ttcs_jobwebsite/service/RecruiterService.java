package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV1;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV3;
import com.example.ttcs_jobwebsite.dto.user.UserOutput;
import com.example.ttcs_jobwebsite.entity.*;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import com.example.ttcs_jobwebsite.repository.*;
import com.example.ttcs_jobwebsite.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecruiterService {
    private final CustomRepository customRepository;
    private final JobRepository jobRepository;
    private final RecruiterJobMapRepository recruiterJobMapRepository;
    private final UserJobMapRepository userJobMapRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PushNotificationService pushNotificationService;

    @Transactional(readOnly = true)
    public Page<JobOutputV1> getJobsBy(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        Page<JobEntity> jobEntityPage = jobRepository.findAllByUserId(userId, pageable);
        if (jobEntityPage.isEmpty() || Objects.isNull(jobEntityPage)) {
            return Page.empty();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return jobEntityPage.map(
                jobEntity -> {
                    JobOutputV1 jobOutputV1 = JobOutputV1.builder()
                            .id(jobEntity.getId())
                            .nameRecruiter(userEntity.getFullName())
                            .imageUrl(userEntity.getImageUrl())
                            .jobName(jobEntity.getJobName())
                            .minSalary(jobEntity.getMinSalary())
                            .maxSalary(jobEntity.getMaxSalary())
                            .address(jobEntity.getAddress())
                            .expirationDate(jobEntity.getExpirationDate().format(formatter))
                            .build();
                    return jobOutputV1;
                }
        );
    }

    @Transactional(readOnly = true)
    public Page<JobOutputV3> getJobsByState(String accessToken, Pageable pageable, String state) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        Page<RecruiterJobMapEntity> recruiterJobMapEntityPage = null;

        if (state.equals("ALL")) {
            recruiterJobMapEntityPage = recruiterJobMapRepository.findAllByRecruiterId(userId, pageable);
        } else {
            recruiterJobMapEntityPage = recruiterJobMapRepository
                    .findAllByRecruiterIdAndState(userId, state, pageable);
        }

        if (recruiterJobMapEntityPage.isEmpty() || Objects.isNull(recruiterJobMapEntityPage)) {
            return Page.empty();
        }

        List<UserJobMapEntity> userJobMapEntities = userJobMapRepository
                .findAllByIdIn(recruiterJobMapEntityPage.stream().map(RecruiterJobMapEntity::getUserJobId).collect(Collectors.toList()));

        Map<Long, UserJobMapEntity> userJobMapEntityMap = userJobMapEntities.stream().collect(
                Collectors.toMap(UserJobMapEntity::getId, Function.identity())
        );

        List<Long> jobIds = userJobMapEntities.stream()
                .map(UserJobMapEntity::getJobId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, JobEntity> jobEntityMap = jobRepository.findAllByIdInWithoutPaging(jobIds)
                .stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                userJobMapEntities.stream().map(UserJobMapEntity::getUserId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return recruiterJobMapEntityPage.map(
                recruiterJobMapEntity -> {
                    UserJobMapEntity userJobMapEntity = userJobMapEntityMap.get(recruiterJobMapEntity.getUserJobId());
                    JobEntity jobEntity = jobEntityMap.get(userJobMapEntity.getJobId());
                    UserEntity userApply = userEntityMap.get(userJobMapEntity.getUserId());
                    UserOutput userOutput = UserOutput.builder()
                            .id(userApply.getId())
                            .fullName(userApply.getFullName())
                            .imageUrl(userApply.getImageUrl())
                            .build();
                    JobOutputV3 jobOutputV3 = JobOutputV3.builder()
                            .id(recruiterJobMapEntity.getId())
                            .userOutput(userOutput)
                            .cvUrl(userJobMapEntity.getCvUrl())
                            .nameRecruiter(userEntity.getFullName())
                            .imageUrl(userEntity.getImageUrl())
                            .jobName(jobEntity.getJobName())
                            .minSalary(jobEntity.getMinSalary())
                            .maxSalary(jobEntity.getMaxSalary())
                            .address(jobEntity.getAddress())
                            .expirationDate(userJobMapEntity.getCreateAt().format(dateTimeFormatter))
                            .state(recruiterJobMapEntity.getState())
                            .build();
                    return jobOutputV3;
                }
        );
    }

    @Transactional
    public ApiResponse<?> acceptApplication(String accessToken, Long recruiterJobId) {
        Long recruiterId = TokenHelper.getUserIdFromToken(accessToken);
        RecruiterJobMapEntity recruiterJobMapEntity = customRepository.getRecruiterJobMap(recruiterJobId);
        if (!recruiterId.equals(recruiterJobMapEntity.getRecruiterId())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        if (!recruiterJobMapEntity.getState().equals(Common.PENDING_APPROVAL)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        UserJobMapEntity userJobMapEntity = customRepository.getUserJobMap(recruiterJobMapEntity.getUserJobId());
        if (!userJobMapEntity.getState().equals(Common.APPLIED)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }
        recruiterJobMapEntity.setState(Common.ACCEPTED);
        recruiterJobMapRepository.save(recruiterJobMapEntity);
        userJobMapEntity.setState(Common.ACCEPTED);
        userJobMapRepository.save(userJobMapEntity);

        CompletableFuture.runAsync(() -> {
            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(recruiterId)
                            .interactId(userJobMapEntity.getUserId())
                            .jobId(userJobMapEntity.getJobId())
                            .hasSeen(Boolean.FALSE)
                            .type(Common.ACCEPTED)
                            .createAt(LocalDateTime.now())
                            .build()
            );

            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(userJobMapEntity.getUserId())
                            .interactId(recruiterId)
                            .jobId(userJobMapEntity.getJobId())
                            .hasSeen(Boolean.FALSE)
                            .type(Common.ACCEPTED)
                            .createAt(LocalDateTime.now())
                            .build()
            );

            String jsonMessage = "{\"title\": \"Thông báo mới\", \"body\": \"Bạn đã ứng thành công cho công việc mới!\"}";
            pushNotificationService.sendNotification(userJobMapEntity.getUserId(), jsonMessage);
        });

        return ApiResponse.builder()
                .code(200)
                .message("Duyệt ứng viên thành công")
                .build();
    }

    @Transactional
    public ApiResponse<?> rejectApplication(String accessToken, Long recruiterJobId) {
        Long recruiterId = TokenHelper.getUserIdFromToken(accessToken);
        RecruiterJobMapEntity recruiterJobMapEntity = customRepository.getRecruiterJobMap(recruiterJobId);
        if (!recruiterId.equals(recruiterJobMapEntity.getRecruiterId())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        if (!recruiterJobMapEntity.getState().equals(Common.PENDING_APPROVAL)) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_STATUS);
        }

        UserJobMapEntity userJobMapEntity = customRepository.getUserJobMap(recruiterJobMapEntity.getUserJobId());
        if (!userJobMapEntity.getState().equals(Common.APPLIED)) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_STATUS);
        }
        recruiterJobMapEntity.setState(Common.REJECTED);
        recruiterJobMapRepository.save(recruiterJobMapEntity);
        userJobMapEntity.setState(Common.REJECTED);
        userJobMapRepository.save(userJobMapEntity);

        CompletableFuture.runAsync(() -> {
            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(recruiterId)
                            .interactId(userJobMapEntity.getUserId())
                            .jobId(userJobMapEntity.getJobId())
                            .hasSeen(Boolean.FALSE)
                            .type(Common.REJECTED)
                            .createAt(LocalDateTime.now())
                            .build()
            );

            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(userJobMapEntity.getUserId())
                            .interactId(recruiterId)
                            .jobId(userJobMapEntity.getJobId())
                            .hasSeen(Boolean.FALSE)
                            .type(Common.REJECTED)
                            .createAt(LocalDateTime.now())
                            .build()
            );

            String jsonMessage = "{\"title\": \"Thông báo mới\", \"body\": \"Rất tiếc, bạn đã ứng tuyển không thành công\"}";
            pushNotificationService.sendNotification(userJobMapEntity.getUserId(), jsonMessage);
        });

        return ApiResponse.builder()
                .code(200)
                .message("Từ chối ứng viện thành công")
                .build();
    }
}
