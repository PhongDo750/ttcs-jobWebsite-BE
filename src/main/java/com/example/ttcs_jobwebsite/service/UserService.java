package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.common.Common;
import com.example.ttcs_jobwebsite.dto.ApiResponse;
import com.example.ttcs_jobwebsite.dto.email.EmailDetails;
import com.example.ttcs_jobwebsite.dto.user.LoginRequest;
import com.example.ttcs_jobwebsite.dto.user.RecoverPassword;
import com.example.ttcs_jobwebsite.dto.user.TokenResponse;
import com.example.ttcs_jobwebsite.dto.user.UserRequest;
import com.example.ttcs_jobwebsite.entity.UserEntity;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import com.example.ttcs_jobwebsite.mapper.UserMapper;
import com.example.ttcs_jobwebsite.redis.PresenceService;
import com.example.ttcs_jobwebsite.repository.CustomRepository;
import com.example.ttcs_jobwebsite.repository.UserRepository;
import com.example.ttcs_jobwebsite.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CustomRepository customRepository;
    private final EmailService emailService;
    private final PresenceService presenceService;

    @Transactional
    public TokenResponse signUp(UserRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (Boolean.TRUE.equals(userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber()))) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        signUpRequest.setPassword(BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt()));
        UserEntity userEntity = userMapper.getEntityFromRequest(signUpRequest);
        userEntity.setImageUrl(Common.IMAGE_DEFAULT);
        userRepository.save(userEntity);
        return TokenResponse.builder()
                .accessToken(TokenHelper.generateToken(userEntity))
                .role(userEntity.getRole())
                .build();
    }

    @Transactional
    public TokenResponse logIn(LoginRequest loginRequest) {
        UserEntity userEntity = userRepository.findByUsername(loginRequest.getUsername());
        if (Objects.isNull(userEntity)) {
            throw new AppException(ErrorCode.USERNAME_NOT_EXISTED);
        }

        if (!BCrypt.checkpw(loginRequest.getPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }
        return TokenResponse.builder()
                .accessToken(TokenHelper.generateToken(userEntity))
                .role(userEntity.getRole())
                .build();
    }

    @Transactional
    public ApiResponse<?> sendCodeToEmail(String userName) {
        UserEntity userEntity = userRepository.findByUsername(userName);
        if (Objects.isNull(userEntity)) {
            throw new AppException(ErrorCode.USERNAME_NOT_EXISTED);
        }
        String code = generateCode();
        EmailDetails emailDetails = EmailDetails.builder()
                .subject("Reset Password")
                .recipient(userEntity.getEmail())
                .messageBody(code)
                .build();
        emailService.sendEmail(emailDetails);
        presenceService.plusCode(String.valueOf(userEntity.getId()), code);
        return ApiResponse.builder().build();
    }

    @Transactional
    public ApiResponse<?> recoverPassword(RecoverPassword recoverPassword) {
        UserEntity userEntity = userRepository.findByUsername(recoverPassword.getUsername());
        if (Objects.isNull(userEntity)) {
            throw new AppException(ErrorCode.USERNAME_NOT_EXISTED);
        }

        if (!recoverPassword.getCode().equals(presenceService.getCode(String.valueOf(userEntity.getId())))) {
            throw new AppException(ErrorCode.CODE_NOT_MATCH);
        } else {
            userEntity.setPassword(BCrypt.hashpw(recoverPassword.getNewPassword(), BCrypt.gensalt()));
            presenceService.delete(String.valueOf(userEntity.getId()));
            userRepository.save(userEntity);
        }
        return ApiResponse.builder().build();
    }

    @Transactional
    public void setRole(String accessToken, String role) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        userEntity.setRole(role);
        userRepository.save(userEntity);
    }

    private String generateCode() {
        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        return uuid.substring(0, 6);
    }
}
