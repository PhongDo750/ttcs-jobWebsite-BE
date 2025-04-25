package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.JobEntity;
import com.example.ttcs_jobwebsite.entity.RecruiterJobMapEntity;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import com.example.ttcs_jobwebsite.entity.UserJobMapEntity;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final RecruiterJobMapRepository recruiterJobMapRepository;
    private final UserJobMapRepository userJobMapRepository;

    public UserEntity getUserBy(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.RECORD_NOT_FOUND)
        );
    }

    public JobEntity getJobBy(Long jobId){
        return jobRepository.findById(jobId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.RECORD_NOT_FOUND)
        );
    }

    public RecruiterJobMapEntity getRecruiterJobMap(Long recruiterJobId){
        return recruiterJobMapRepository.findById(recruiterJobId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.RECORD_NOT_FOUND)
        );
    }

    public UserJobMapEntity getUserJobMap(Long userJobId){
        return userJobMapRepository.findById(userJobId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.RECORD_NOT_FOUND)
        );
    }
}
