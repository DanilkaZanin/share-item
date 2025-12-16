package com.example.demo.controller;

import com.example.demo.dto.request.UserCreateRequest;
import com.example.demo.dto.request.UserSignInRequest;
import com.example.demo.dto.response.JwtAuthenticationResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityController {
    UserAuthService userAuthService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signUp(@RequestBody @Validated UserCreateRequest userCreateRequest) {
        return userAuthService.saveUser(userCreateRequest);
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public JwtAuthenticationResponse signIn(@RequestBody @Validated UserSignInRequest authRequest) {
        return userAuthService.getJwtToken(authRequest);
    }
}
