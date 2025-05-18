package com.example.ttcs_jobwebsite.controller;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV1;
import com.example.ttcs_jobwebsite.dto.user.TokenResponse;
import com.example.ttcs_jobwebsite.dto.user.UserOutputV2;
import com.example.ttcs_jobwebsite.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "Đăng nhập tài khoản người dùng")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> loginById(@RequestParam Long userId,
                                                @RequestHeader(Common.AUTHORIZATION) String accessToken) {
        return adminService.logIn(userId, accessToken);
    }

    @Operation(summary = "Lấy ra tất cả user và recruiter")
    @GetMapping
    public Page<UserOutputV2> getAllUsers(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                          @ParameterObject Pageable pageable) {
        return adminService.getAllUsers(accessToken, pageable);
    }

    @Operation(summary = "Lấy ra thông tin user")
    @GetMapping("/get-user")
    public ApiResponse<UserOutputV2> getUserByEmail(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                                    @RequestParam String email) {
        return adminService.getUserByEmail(accessToken, email);
    }

    @Operation(summary = "Đếm số lượng job đăng tuyển trong tháng")
    @GetMapping("/job-posted")
    public ApiResponse<Integer> countJobsPostedInMonth(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                                       @RequestParam String month,
                                                       @RequestParam String year) {
        return adminService.countJobsPostedInMonth(accessToken, month, year);
    }

    @Operation(summary = "Đếm số lượng người ứng tuyển trong tháng")
    @GetMapping("/user-applied")
    public ApiResponse<Integer> countUserAppliedInMonth(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                                       @RequestParam String month,
                                                       @RequestParam String year) {
        return adminService.countUsersAppliedInMonth(accessToken, month, year);
    }

    @Operation(summary = "Đếm tổng số lượng user và role")
    @GetMapping("/counts")
    public ApiResponse<Object> countUsersByRole(@RequestHeader(Common.AUTHORIZATION) String accessToken) {
        return adminService.countUsersByRole(accessToken);
    }

    @Operation(summary = "Xóa user")
    @DeleteMapping("/delete")
    public ApiResponse<?> deleteUser(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                     @RequestParam Long userId) {
        return adminService.deleteUser(accessToken, userId);
    }

    @Operation(summary = "Lấy ra job theo tháng")
    @GetMapping("/jobs")
    public Page<JobOutputV1> getJobsPostedInMonth(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                                  @RequestParam String year,
                                                  @RequestParam String month,
                                                  @ParameterObject Pageable pageable) {
        return adminService.getJobsPostedInMonth(accessToken, year, month, pageable);
    }

    @Operation(summary = "Xóa job")
    @DeleteMapping("/job")
    public ApiResponse<?> deleteJob(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                    @RequestParam Long jobId) {
        return adminService.deleteJob(accessToken, jobId);
    }

    @Operation(summary = "Lấy job")
    @GetMapping("/job")
    public ApiResponse<JobOutputV1> findJob(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                           @RequestParam Long jobId) {
        return adminService.findJob(accessToken, jobId);
    }

}
