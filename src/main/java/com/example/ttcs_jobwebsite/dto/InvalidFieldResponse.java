package com.example.ttcs_jobwebsite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvalidFieldResponse<T> {
    @Builder.Default
    private int code = 400;
    private String message = "INVALID_FIELD";
    private T error;
}
