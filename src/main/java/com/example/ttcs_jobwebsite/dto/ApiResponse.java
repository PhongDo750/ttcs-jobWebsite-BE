package com.example.ttcs_jobwebsite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private int code = 200;
    private String message = "OK";
    private T data;
}
