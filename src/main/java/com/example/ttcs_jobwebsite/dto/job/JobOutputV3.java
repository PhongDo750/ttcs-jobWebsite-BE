package com.example.ttcs_jobwebsite.dto.job;

import com.example.ttcs_jobwebsite.dto.user.UserOutput;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class JobOutputV3 {
    private Long id;
    private UserOutput userOutput;
    private String cvUrl;
    private String nameRecruiter;
    private String imageUrl;
    private String jobName;
    private Double minSalary;
    private Double maxSalary;
    private String address;
    private String expirationDate;
    private String state;
}
