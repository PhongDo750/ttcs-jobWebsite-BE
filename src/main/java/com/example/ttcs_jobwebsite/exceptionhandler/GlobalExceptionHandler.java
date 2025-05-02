package com.example.ttcs_jobwebsite.exceptionhandler;

import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.InvalidFieldResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> appException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(exception.getHttpStatus()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InvalidFieldResponse> handleValidationErrors(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        // Sử dụng LinkedHashMap để giữ nguyên thứ tự các field
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : fieldErrors) {
            // Nếu một field có nhiều lỗi thì chỉ lấy lỗi đầu tiên (nếu muốn), hoặc có thể xử lý thêm
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                InvalidFieldResponse.builder()
                        .code(400)
                        .message("INVALID_FIELD")
                        .error(errors)
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<InvalidFieldResponse> handleInvalidFormat(HttpMessageNotReadableException ex) {
        String errorMessage = "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại định dạng.";

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            List<JsonMappingException.Reference> path = ife.getPath();
            if (!path.isEmpty()) {
                String fieldName = path.get(0).getFieldName();
                errorMessage = "Trường '" + fieldName + "' phải nhập đúng kiểu dữ liệu.";
            }
        }

        return ResponseEntity.badRequest().body(
                InvalidFieldResponse.builder()
                        .code(400)
                        .message("INVALID_FIELD")
                        .error(Map.of("format_error", errorMessage))
                        .build()
        );
    }
}
