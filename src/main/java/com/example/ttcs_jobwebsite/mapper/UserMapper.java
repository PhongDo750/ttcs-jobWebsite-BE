package com.example.ttcs_jobwebsite.mapper;

import com.example.ttcs_jobwebsite.dto.user.UserRequest;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserEntity getEntityFromRequest(UserRequest signUpRequest);
}
