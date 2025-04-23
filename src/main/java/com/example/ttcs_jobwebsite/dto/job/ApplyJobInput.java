package com.example.ttcs_jobwebsite.dto.job;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApplyJobInput {
    @NotNull(message = "jobId không được để trống")
    @Positive(message = "jobId phải là số dương")
    private Long jobId;

    @NotEmpty(message = "Giới thiệu không được để trống")
    private String introductions;
}
