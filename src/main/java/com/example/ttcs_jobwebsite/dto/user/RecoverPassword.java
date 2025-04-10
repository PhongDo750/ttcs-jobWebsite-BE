package com.example.ttcs_jobwebsite.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecoverPassword {
    private String code;
    private String newPassword;
    private String username;
}
