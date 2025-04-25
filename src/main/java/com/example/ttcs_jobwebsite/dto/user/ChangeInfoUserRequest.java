package com.example.ttcs_jobwebsite.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeInfoUserRequest {
    private String fullName;
    private String birthdayString;
    private String phoneNumber;
    private String email;
    private String address;
    private String description;
}
