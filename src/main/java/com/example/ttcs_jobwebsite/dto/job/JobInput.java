package com.example.ttcs_jobwebsite.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobInput {
    private String jobName;
    private String occupationName;
    private String experience;
    private Integer headCount;
    private LocalDateTime expirationDate;
    private String province;
    private String jobType;
    private String jobLevel;
    private Double minSalary;
    private Double maxSalary;
    private String educationLevel;
    private String descriptions;
    private String requiredJobList;
    private String employeeBenefitList;
    private String address;
}
