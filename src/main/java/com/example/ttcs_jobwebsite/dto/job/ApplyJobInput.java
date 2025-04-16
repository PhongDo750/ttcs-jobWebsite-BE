package com.example.ttcs_jobwebsite.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApplyJobInput {
    private Long jobId;
    private String introductions;
}
