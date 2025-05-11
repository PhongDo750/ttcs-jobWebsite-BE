package com.example.ttcs_jobwebsite.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CountUserOutput {
    private int countRoleUser;
    private int countRoleRecruiter;
}
