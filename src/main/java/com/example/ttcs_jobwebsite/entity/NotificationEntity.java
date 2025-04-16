package com.example.ttcs_jobwebsite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_notification")
@Builder
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long interactId;
    private Long jobId;
    private String type;
    private Boolean hasSeen;
    private LocalDateTime createAt;
}
