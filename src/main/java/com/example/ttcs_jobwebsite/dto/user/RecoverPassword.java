package com.example.ttcs_jobwebsite.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecoverPassword {
    @NotEmpty(message = "Mã xác nhận không được để trống")
    private String code;

    @NotEmpty(message = "Mật khẩu mới không được để trống")
    private String newPassword;

    @NotEmpty(message = "Tên người dùng không được để trống")
    private String username;
}
