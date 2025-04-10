package com.example.ttcs_jobwebsite.controller;

import com.example.ttcs_jobwebsite.dto.token.ResponseToken;
import com.example.ttcs_jobwebsite.service.OAuth2Service;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/google-url")
    public ResponseEntity<String> generateAuthUrl(){
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(oAuth2Service.generateAuthUrl());
    }

    @PostMapping("/logIn")
    public ResponseEntity<ResponseToken> getAccessToken(@RequestParam String code) throws ParseException {
        return new ResponseEntity<>(oAuth2Service.logIn(code), HttpStatus.OK);
    }
}
