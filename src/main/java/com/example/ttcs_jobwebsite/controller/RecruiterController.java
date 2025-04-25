package com.example.ttcs_jobwebsite.controller;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV1;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV3;
import com.example.ttcs_jobwebsite.service.RecruiterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/recruiter")
public class RecruiterController {
    private final RecruiterService recruiterService;

    @Operation(summary = "Lấy ra các job")
    @GetMapping
    public Page<JobOutputV1> getJobsBy(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                       @ParameterObject Pageable pageable) {
        return recruiterService.getJobsBy(accessToken, pageable);
    }
}
