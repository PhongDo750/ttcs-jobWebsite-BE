package com.example.ttcs_jobwebsite.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeInfoUserRequest {
    @NotBlank(message = "Tên công ty không được để trống")
    private String fullName;

    private String birthdayString; // Ngày sinh thì cho phép null => bạn tự xử lý format riêng

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Mô tả công ty không được để trống")
    private String description;
}
