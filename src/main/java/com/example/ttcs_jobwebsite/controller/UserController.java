package com.example.ttcs_jobwebsite.controller;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.user.LoginRequest;
import com.example.ttcs_jobwebsite.dto.user.RecoverPassword;
import com.example.ttcs_jobwebsite.dto.user.TokenResponse;
import com.example.ttcs_jobwebsite.dto.user.UserRequest;
import com.example.ttcs_jobwebsite.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Đăng ký tài khoản")
    @PostMapping("sign-up")
    public ApiResponse<TokenResponse> signUp(@RequestBody UserRequest signUpRequest){
        return ApiResponse.<TokenResponse>builder()
                .data(userService.signUp(signUpRequest))
                .build();
    }

    @PostMapping("log-in")
    public ResponseEntity<TokenResponse> logIn(@RequestBody @Valid LoginRequest logInRequest) {
        return new ResponseEntity<>(userService.logIn(logInRequest), HttpStatus.OK);
    }

    @Operation(summary = "Lấy code để reset password")
    @PostMapping("/send-code-email")
    public ApiResponse<?> sendCodeToEmail(@RequestParam String username) {
        return userService.sendCodeToEmail(username);
    }

    @Operation(summary = "Lấy lại mật khẩu")
    @PostMapping("/recover-password")
    public ApiResponse<?> recoverPassword(@RequestBody RecoverPassword recoverPassword) {
        return userService.recoverPassword(recoverPassword);
    }

    @Operation(summary = "Chọn role")
    @PostMapping("/set-role")
    public void setRole(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                        @RequestParam String role) {
        userService.setRole(accessToken, role);
    }
}
