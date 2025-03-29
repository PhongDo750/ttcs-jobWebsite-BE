package com.example.ttcs_jobwebsite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tbl_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String imageUrl;
    private String backgroundImage;
    private OffsetDateTime birthday;
    private String gender;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String googleId;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;
}
