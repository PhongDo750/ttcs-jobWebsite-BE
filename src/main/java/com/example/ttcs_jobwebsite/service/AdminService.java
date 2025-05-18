package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV1;
import com.example.ttcs_jobwebsite.dto.user.CountUserOutput;
import com.example.ttcs_jobwebsite.dto.user.LoginRequest;
import com.example.ttcs_jobwebsite.dto.user.TokenResponse;
import com.example.ttcs_jobwebsite.dto.user.UserOutputV2;
import com.example.ttcs_jobwebsite.entity.JobEntity;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import com.example.ttcs_jobwebsite.entity.UserJobMapEntity;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import com.example.ttcs_jobwebsite.repository.*;
import com.example.ttcs_jobwebsite.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CustomRepository customRepository;
    private final JobRepository jobRepository;
    private final UserJobMapRepository userJobMapRepository;
    private final RecruiterJobMapRepository recruiterJobMapRepository;
    private final JobLikeMapRepository jobLikeMapRepository;
    private final NotificationRepository notificationRepository;

    public ApiResponse<TokenResponse> logIn(Long userId, String accessToken) {

        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);
        if(!adminEntity.getRole().equals("ADMIN")) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        UserEntity userEntity = customRepository.getUserBy(userId);

        return ApiResponse.<TokenResponse>builder()
                .code(200)
                .message("Đăng nhập thành công")
                .data(TokenResponse.builder()
                        .accessToken(TokenHelper.generateToken(userEntity))
                        .role(userEntity.getRole())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<UserOutputV2> getAllUsers(String accessToken, Pageable pageable) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        Page<UserEntity> userEntityPage = userRepository.findAllUserWithoutAdmin(Common.ADMIN, pageable);
        return userEntityPage.map(
                userEntity -> {
                    UserOutputV2 userOutputV2 = UserOutputV2.builder()
                            .id(userEntity.getId())
                            .username(userEntity.getUsername())
                            .fullName(userEntity.getFullName())
                            .email(userEntity.getEmail())
                            .role(userEntity.getRole())
                            .birthday(userEntity.getBirthday())
                            .build();
                    return userOutputV2;
                }
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<UserOutputV2> getUserByEmail(String accessToken, String email) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        UserEntity userEntity = userRepository.findByEmail(email);
        return ApiResponse.<UserOutputV2>builder()
                .data(UserOutputV2.builder()
                        .id(userEntity.getId())
                        .username(userEntity.getUsername())
                        .fullName(userEntity.getFullName())
                        .email(userEntity.getEmail())
                        .role(userEntity.getRole())
                        .birthday(userEntity.getBirthday())
                        .build())
                .code(200)
                .message("OK")
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<Integer> countJobsPostedInMonth(String accessToken, String month, String year) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        int count = jobRepository.countJobsPostedInMonth(Integer.parseInt(year), Integer.parseInt(month));
        System.out.println(count);
        return ApiResponse.<Integer>builder()
                .message("OK")
                .code(200)
                .data(count)
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<Integer> countUsersAppliedInMonth(String accessToken, String month, String year) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        return ApiResponse.<Integer>builder()
                .message("OK")
                .code(200)
                .data(userJobMapRepository.countUsersAppliedInMonth(Integer.parseInt(year), Integer.parseInt(month)))
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse<Object> countUsersByRole(String accessToken) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        List<UserEntity> userEntities = userRepository.findAll();
        List<UserEntity> recruiterEntity = userEntities.stream().filter(
                userEntity -> userEntity.getRole().equals(Common.RECRUITER)
        ).collect(Collectors.toList());

        return ApiResponse.builder()
                .code(200)
                .message("OK")
                .data(
                        CountUserOutput.builder()
                                .countRoleUser(userEntities.size() - recruiterEntity.size() - 1)
                                .countRoleRecruiter(recruiterEntity.size())
                                .build()
                )
                .build();
    }

    @Transactional
    public ApiResponse<?> deleteUser(String accessToken, Long userId) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        UserEntity userEntity = customRepository.getUserBy(userId);

        notificationRepository.deleteAllByUserIdOrInteractId(userId, userId);

        if (userEntity.getRole().equals(Common.USER)) {
            userJobMapRepository.deleteAllByUserId(userId);
            jobLikeMapRepository.deleteAllByUserId(userId);
        }

        if (userEntity.getRole().equals(Common.RECRUITER)) {
            List<Long> jobIds = jobRepository.findAllByUserIdWithoutPaging(userId).stream()
                    .map(JobEntity::getId).collect(Collectors.toList());

            recruiterJobMapRepository.deleteAllByRecruiterId(userId);

            userJobMapRepository.deleteAllByJobIdIn(jobIds);

            jobLikeMapRepository.deleteAllByJobIdIn(jobIds);

            jobRepository.deleteAllByUserId(userId);
        }

        userRepository.deleteById(userId);

        return ApiResponse.builder()
                .code(200)
                .message("Xóa user thành công")
                .build();
    }

    @Transactional(readOnly = true)
    public Page<JobOutputV1> getJobsPostedInMonth(String accessToken, String year, String month, Pageable pageable) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        Page<JobEntity> jobEntityPage = jobRepository.findJobsPostedInMonth(Integer.parseInt(year), Integer.parseInt(month), pageable);

        Map<Long, UserEntity> recruiterEntityMap = userRepository.findAllByIdIn(
                jobEntityPage.stream().map(JobEntity::getUserId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return jobEntityPage.map(
                jobEntity -> {
                    UserEntity recruiterEntity = recruiterEntityMap.get(jobEntity.getUserId());

                    return JobOutputV1.builder()
                            .id(jobEntity.getId())
                            .jobName(jobEntity.getJobName())
                            .nameRecruiter(recruiterEntity.getFullName())
                            .createdAt(jobEntity.getCreateAt().format(dateTimeFormatter))
                            .build();
                }
        );
    }

    @Transactional
    public ApiResponse<?> deleteJob(String accessToken, Long jobId) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        notificationRepository.deleteAllByJobId(jobId);

        List<Long> userJobIds = userJobMapRepository.findAllByJobId(jobId).stream()
                .map(UserJobMapEntity::getId).collect(Collectors.toList());

        recruiterJobMapRepository.deleteAllByUserJobIdIn(userJobIds);

        userJobMapRepository.deleteAllByJobId(jobId);

        jobLikeMapRepository.deleteAllByJobId(jobId);

        jobRepository.deleteById(jobId);

        return ApiResponse.builder()
                .code(200)
                .message("Xóa công việc thành công")
                .build();
    }

    @Transactional
    public ApiResponse<JobOutputV1> findJob(String accessToken, Long jobId) {
        Long adminId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity adminEntity = customRepository.getUserBy(adminId);

        if (!adminEntity.getRole().equals(Common.ADMIN)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        JobEntity jobEntity = customRepository.getJobBy(jobId);

        UserEntity recruiterEntity = customRepository.getUserBy(jobEntity.getUserId());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return ApiResponse.<JobOutputV1>builder()
                .code(200)
                .message("OK")
                .data(JobOutputV1.builder()
                        .id(jobEntity.getId())
                        .jobName(jobEntity.getJobName())
                        .nameRecruiter(recruiterEntity.getFullName())
                        .createdAt(jobEntity.getCreateAt().format(dateTimeFormatter))
                        .build())
                .build();
    }
}
