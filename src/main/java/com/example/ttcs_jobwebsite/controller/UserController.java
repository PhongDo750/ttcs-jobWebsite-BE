package com.example.ttcs_jobwebsite.controller;

import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.user.LoginRequest;
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
}
