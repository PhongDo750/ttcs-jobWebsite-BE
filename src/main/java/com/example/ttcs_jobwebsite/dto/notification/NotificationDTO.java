package com.example.ttcs_jobwebsite.dto.notification;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NotificationDTO {
    private Long id;
    private Long interactId;
    private Long jobId;
    private String fullName;
    private String jobName;
    private String type;
    private Boolean hasSeen;
}
