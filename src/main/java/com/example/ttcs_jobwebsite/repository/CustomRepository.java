package com.example.ttcs_jobwebsite.repository;

import com.example.ttcs_jobwebsite.entity.JobEntity;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public UserEntity getUserBy(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
        );
    }

    public JobEntity getJobBy(Long jobId){
        return jobRepository.findById(jobId).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
        );
    }
}
