package com.example.ttcs_jobwebsite.controller;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.notification.NotificationDTO;
import com.example.ttcs_jobwebsite.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<Page<NotificationDTO>> getNotifications(@ParameterObject Pageable pageable,
                                                               @RequestHeader(Common.AUTHORIZATION) String accessToken) {
        return notificationService.getNotifications(pageable, accessToken);
    }

    @GetMapping("/count")
    public ApiResponse<Integer> countNotificationsHasNotSeen(@RequestHeader(Common.AUTHORIZATION) String accessToken) {
        return notificationService.countNotificationsHasNotSeen(accessToken);
    }
}
