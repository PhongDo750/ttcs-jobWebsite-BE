package com.example.ttcs_jobwebsite.dto.job;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class JobOutputV1 {
    private Long id;
    private String nameRecruiter;
    private String imageUrl;
    private String jobName;
    private Double minSalary;
    private Double maxSalary;
    private String address;
    private String expirationDate;
    private Boolean hasLiked;
    private String createdAt;
}
