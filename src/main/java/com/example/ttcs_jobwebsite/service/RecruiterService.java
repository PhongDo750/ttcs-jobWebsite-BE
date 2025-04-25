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
}
