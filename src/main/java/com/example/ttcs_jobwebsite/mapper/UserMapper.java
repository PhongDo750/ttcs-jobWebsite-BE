package com.example.ttcs_jobwebsite.mapper;

import com.example.ttcs_jobwebsite.dto.user.ChangeInfoUserRequest;
import com.example.ttcs_jobwebsite.dto.user.UserOutputV2;
import com.example.ttcs_jobwebsite.dto.user.UserRequest;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {
    UserEntity getEntityFromRequest(UserRequest signUpRequest);
    void updateEntityFromInput(@MappingTarget UserEntity userEntity, ChangeInfoUserRequest changeInfoUserRequest);
    UserOutputV2 getOutputFromEntity(UserEntity userEntity);
}
