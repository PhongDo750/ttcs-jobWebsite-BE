package com.example.ttcs_jobwebsite.exceptionhandler;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USERNAME_EXISTED(400, "username đã tồn tại"),
    EMAIL_EXISTED(400, "email đã tồn tại"),
    PHONE_EXISTED(400, "số điện thoại đã tồn tại"),
    USERNAME_NOT_EXISTED(404, "username không tồn tại"),
    UN_AUTHORIZATION(401, "Không có quyền truy cập"),
    INCORRECT_PASSWORD(400, "Sai mật khẩu"),
    CODE_NOT_MATCH(400, "Mã xác nhận không hợp lệ"),
    RECORD_NOT_FOUND(404, "Tài khoản không tồn tại"),
    FILE_NOT_FOUND(400, "Nhập file PDF"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message ;
}
