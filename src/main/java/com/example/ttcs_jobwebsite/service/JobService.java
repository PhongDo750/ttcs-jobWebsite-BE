package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.job.JobInput;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV1;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV2;
import com.example.ttcs_jobwebsite.dto.user.UserOutput;
import com.example.ttcs_jobwebsite.entity.JobEntity;
import com.example.ttcs_jobwebsite.entity.JobLikeMapEntity;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import com.example.ttcs_jobwebsite.entity.UserJobMapEntity;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import com.example.ttcs_jobwebsite.helper.JobSpecification;
import com.example.ttcs_jobwebsite.mapper.JobMapper;
import com.example.ttcs_jobwebsite.repository.*;
import com.example.ttcs_jobwebsite.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final CustomRepository customRepository;
    private final JobMapper jobMapper;
    private final UserRepository userRepository;
    private final JobLikeMapRepository jobLikeMapRepository;
    private final UserJobMapRepository userJobMapRepository;
    private final RecruiterJobMapRepository recruiterJobMapRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public ApiResponse<?> createJob(String accessToken, JobInput jobInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (!userEntity.getRole().equals(Common.RECRUITER)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UN_AUTHORIZATION);
        }

        JobEntity jobEntity = jobMapper.getEntityFromInput(jobInput);

        if (jobInput.getMaxSalary() < jobInput.getMinSalary()) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_SALARY);
        }

        jobEntity.setDescription(jobInput.getDescriptions()
                .replaceAll("<li>|</li>|<ul>|</ul>|<br />", "")
        );
        jobEntity.setRequiredJob(jobInput.getRequiredJobList()
                .replaceAll("<li>|</li>|<ul>|</ul>|<br />", "")
        );
        jobEntity.setEmployeeBenefit(jobInput.getEmployeeBenefitList()
                .replaceAll("<li>|</li>|<ul>|</ul>|<br />", "")
        );
        jobEntity.setUserId(userId);
        jobEntity.setCreateAt(LocalDateTime.now());
        jobRepository.save(jobEntity);
        return ApiResponse.builder()
                .code(200)
                .message("OK")
                .build();
    }

    @Transactional
    public ApiResponse<?> updateJob(String accessToken, JobInput jobInput, Long jobId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (!userEntity.getRole().equals(Common.RECRUITER)) {
            throw new AppException(ErrorCode.UN_AUTHORIZATION);
        }

        JobEntity jobEntity = customRepository.getJobBy(jobId);
        if (!jobEntity.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UN_AUTHORIZATION);
        }

        if (jobInput.getMaxSalary() < jobInput.getMinSalary()) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_SALARY);
        }

        jobMapper.updateEntityFromInput(jobEntity, jobInput);
        jobRepository.save(jobEntity);

        return ApiResponse.builder()
                .code(200)
                .message("OK")
                .build();
    }

    @Transactional
    public ApiResponse<?> deleteJob(String accessToken, Long jobId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (!userEntity.getRole().equals(Common.RECRUITER)) {
            throw new AppException(ErrorCode.UN_AUTHORIZATION);
        }

        JobEntity jobEntity = customRepository.getJobBy(jobId);
        if (!jobEntity.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UN_AUTHORIZATION);
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
                .message("OK")
                .build();
    }

    @Transactional(readOnly = true)
    public Page<JobOutputV1> filterJobs(Pageable pageable, String jobName, String occupationName,
                                        String experience, String province, String jobType, String jobLevel,
                                        Double minSalary, Double maxSalary, String educationLevel,
                                        String accessToken) {
        Specification<JobEntity> jobSpecification =
                JobSpecification.filterJobs(jobName, occupationName, experience, province, jobType, jobLevel, minSalary, maxSalary, educationLevel);

        Page<JobEntity> jobEntityPage = jobRepository.findAll(jobSpecification, pageable);
        if (jobEntityPage.isEmpty() || Objects.isNull(jobEntityPage)) {
            return Page.empty();
        }

        return getJobs(jobEntityPage, accessToken);
    }

    @Transactional(readOnly = true)
    public ApiResponse<JobOutputV2> getDescriptionJob(Long jobId, String accessToken) {
        JobEntity jobEntity = customRepository.getJobBy(jobId);
        UserEntity recruiterEntity = customRepository.getUserBy(jobEntity.getUserId());
        UserOutput userOutput = UserOutput.builder()
                .id(recruiterEntity.getId())
                .fullName(recruiterEntity.getFullName())
                .imageUrl(recruiterEntity.getImageUrl())
                .build();

        JobOutputV2 jobOutputV2 = jobMapper.getOutputV2FromEntity(jobEntity);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = jobEntity.getExpirationDate().format(formatter);
        jobOutputV2.setId(jobEntity.getId());
        jobOutputV2.setExpirationDate(formattedDate);
        jobOutputV2.setUserOutput(userOutput);
        jobOutputV2.setDescriptions(jobEntity.getDescription());
        jobOutputV2.setRequiredJobList(jobEntity.getRequiredJob());
        jobOutputV2.setEmployeeBenefitList(jobEntity.getEmployeeBenefit());
        jobOutputV2.setHasLiked(Boolean.FALSE);

        if (Objects.nonNull(accessToken)) {
            Long userId = TokenHelper.getUserIdFromToken(accessToken);
            if (Boolean.TRUE.equals(jobLikeMapRepository.existsByUserIdAndJobId(userId, jobEntity.getId()))) {
                jobOutputV2.setHasLiked(Boolean.TRUE);
            }
        }

        return ApiResponse.<JobOutputV2>builder()
                .code(200)
                .message("OK")
                .data(jobOutputV2)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<JobOutputV1> getLikedJobs(String accessToken, Pageable pageable) {
        Long userId  = TokenHelper.getUserIdFromToken(accessToken);
        List<Long> jobIds = jobLikeMapRepository.findAllByUserId(userId).stream()
                .map(JobLikeMapEntity::getJobId).collect(Collectors.toList());

        Page<JobEntity> jobEntityPage = jobRepository.findAllByIdIn(jobIds, pageable);
        if (Objects.isNull(jobEntityPage) || jobEntityPage.isEmpty()) {
            return Page.empty();
        }

        Set<Long> userIds = jobEntityPage.stream().map(JobEntity::getUserId).collect(Collectors.toSet());
        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return jobEntityPage.map(
                jobEntity -> {
                    UserEntity userEntity = userEntityMap.get(jobEntity.getUserId());
                    JobOutputV1 jobOutputV1 = JobOutputV1.builder()
                            .id(jobEntity.getId())
                            .nameRecruiter(userEntity.getFullName())
                            .imageUrl(userEntity.getImageUrl())
                            .jobName(jobEntity.getJobName())
                            .minSalary(jobEntity.getMinSalary())
                            .maxSalary(jobEntity.getMaxSalary())
                            .address(jobEntity.getAddress())
                            .expirationDate(jobEntity.getExpirationDate().format(dateTimeFormatter))
                            .hasLiked(Boolean.TRUE)
                            .build();
                    return jobOutputV1;
                }
        );
    }

    @Transactional(readOnly = true)
    public Page<JobOutputV1> getJobsByState(Pageable pageable, String state, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        Page<UserJobMapEntity> userJobMapEntityPage = null;
        if (state.equals("ALL")) {
            userJobMapEntityPage = userJobMapRepository.findAllByUserId(userId, pageable);
        } else {
            userJobMapEntityPage = userJobMapRepository.findAllByUserIdAndState(userId, state, pageable);
        }
        if (Objects.isNull(userJobMapEntityPage) || userJobMapEntityPage.isEmpty()) {
            return Page.empty();
        }

        List<Long> jobIds = userJobMapEntityPage.stream().map(UserJobMapEntity::getJobId).collect(Collectors.toList());
        List<JobEntity> jobEntities = jobRepository.
                findAllByIdInWithoutPaging(jobIds);
        Map<Long, JobEntity> jobEntityMap = jobEntities.stream()
                .collect(Collectors.toMap(JobEntity::getId, Function.identity()));
        Map<Long, UserEntity> recruiterEntityMap = userRepository.findAllByIdIn(
                jobEntities.stream().map(JobEntity::getUserId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return userJobMapEntityPage.map(
                userJobMapEntity -> {
                    JobEntity jobEntity = jobEntityMap.get(userJobMapEntity.getJobId());
                    UserEntity recruiterEntity = recruiterEntityMap.get(jobEntity.getUserId());
                    JobOutputV1 jobOutputV1 = JobOutputV1.builder()
                            .id(jobEntity.getId())
                            .nameRecruiter(recruiterEntity.getFullName())
                            .imageUrl(recruiterEntity.getImageUrl())
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

    private Page<JobOutputV1> getJobs(Page<JobEntity> jobEntityPage, String accessToken) {
        Set<Long> recruiterIds = jobEntityPage.stream().map(JobEntity::getUserId).collect(Collectors.toSet());
        Map<Long, UserEntity> recruiterEntityMap =  userRepository.findAllByIdIn(recruiterIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        Long userId = null;
        Map<Long, JobLikeMapEntity> jobLikeEntityMap;
        if (!Objects.isNull(accessToken)) {
            userId = TokenHelper.getUserIdFromToken(accessToken);
            jobLikeEntityMap = jobLikeMapRepository.findAllByUserId(userId).stream()
                    .collect(Collectors.toMap(JobLikeMapEntity::getJobId, Function.identity()));
        } else {
            jobLikeEntityMap = new HashMap<>();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return jobEntityPage.map(
                jobEntity -> {
                    UserEntity recruiter = recruiterEntityMap.get(jobEntity.getUserId());
                    JobOutputV1 jobOutputV1 = JobOutputV1.builder()
                            .id(jobEntity.getId())
                            .nameRecruiter(recruiter.getFullName())
                            .imageUrl(recruiter.getImageUrl())
                            .jobName(jobEntity.getJobName())
                            .minSalary(jobEntity.getMinSalary())
                            .maxSalary(jobEntity.getMaxSalary())
                            .address(jobEntity.getAddress())
                            .expirationDate(jobEntity.getExpirationDate().format(formatter))
                            .build();

                    if (!Objects.isNull(jobLikeEntityMap)) {
                        JobLikeMapEntity jobLikeMapEntity = jobLikeEntityMap.get(jobEntity.getId());
                        jobOutputV1.setHasLiked(
                                jobLikeMapEntity != null ? Boolean.TRUE : Boolean.FALSE
                        );
                    }
                    return jobOutputV1;
                }
        );
    }
}
