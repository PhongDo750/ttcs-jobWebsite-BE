package com.example.ttcs_jobwebsite.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String fullName;
    @NotEmpty
    private String email;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private String address;
    @NotEmpty
    private String role;
}
