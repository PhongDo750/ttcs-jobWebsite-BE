package com.example.ttcs_jobwebsite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_user_job_map")
@Builder
public class UserJobMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long jobId;
    @Column(columnDefinition = "TEXT")
    private String introduction;
    private String cvUrl;
    private String state;
    private LocalDateTime createAt;
}
