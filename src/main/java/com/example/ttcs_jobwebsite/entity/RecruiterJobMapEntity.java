package com.example.ttcs_jobwebsite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_recruiter_job_map")
@Builder
public class RecruiterJobMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recruiterId;
    private Long userJobId;
    private String state;
    private LocalDateTime createAt;
}
