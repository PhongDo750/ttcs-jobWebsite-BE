package com.example.ttcs_jobwebsite.dto.email;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EmailDetails {
    private String recipient;
    private String messageBody;
    private String subject;
}
