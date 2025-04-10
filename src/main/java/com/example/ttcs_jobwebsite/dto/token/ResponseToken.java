package com.example.ttcs_jobwebsite.dto.token;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResponseToken {
    private String accessTokenOP;
    private String accessTokenRP;
    private String role;
}
