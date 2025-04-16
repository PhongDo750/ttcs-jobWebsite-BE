package com.example.ttcs_jobwebsite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_job")
@Builder
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
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
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String requiredJob;
    @Column(columnDefinition = "TEXT")
    private String employeeBenefit;
    private String address;
    private LocalDateTime createAt;
}
