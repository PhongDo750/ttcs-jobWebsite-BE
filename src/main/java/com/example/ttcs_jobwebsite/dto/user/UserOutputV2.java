package com.example.ttcs_jobwebsite.dto.user;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOutputV2 {
    private Long id;
    private String username;
    private String fullName;
    private String imageUrl;
    private String backgroundImage;
    private String description;
    private OffsetDateTime birthday;
    private String address;
    private String phoneNumber;
    private String email;
    private String role;
}
