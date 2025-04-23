package com.example.ttcs_jobwebsite.exceptionhandler;

import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.InvalidFieldResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
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
        FieldError firstError = exception.getBindingResult().getFieldErrors().get(0);
        String field = firstError.getField();
        String message = firstError.getDefaultMessage();

        Map<String, String> error = new HashMap<>();
        error.put(field, message);
        return ResponseEntity.badRequest().body(
                InvalidFieldResponse.builder()
                                    .code(400)
                                    .message("INVALID_FIELD")
                                    .error(error)
                                    .build()
        );
    }
}
