package com.example.ttcs_jobwebsite.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginRequest {
    @NotEmpty(message = "Tên tài khoản không được để trống")
    private String username;
    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;
}
