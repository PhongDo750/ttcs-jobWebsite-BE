package com.example.ttcs_jobwebsite.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotEmpty(message = "username không được để trống")
    private String username;
    @NotEmpty(message = "mật khẩu không được để trống")
    private String password;
    @NotEmpty(message = "Tên đầy đủ không được để trống")
    private String fullName;
    @NotEmpty(message = "Email không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "Email phải là địa chỉ Gmail hợp lệ")
    private String email;
    @NotEmpty(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải đúng định dạng và có 10 chữ số")
    private String phoneNumber;
    @NotEmpty(message = "Địa chỉ không được để trống")
    private String address;
    @NotEmpty
    private String role;
}
