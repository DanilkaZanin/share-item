package com.example.demo.service;

import com.example.demo.dto.request.UserCreateRequest;
import com.example.demo.dto.request.UserSignInRequest;
import com.example.demo.dto.response.JwtAuthenticationResponse;
import com.example.demo.dto.response.UserResponse;

public interface UserAuthService {
    UserResponse saveUser(UserCreateRequest userCreateRequest);
    JwtAuthenticationResponse getJwtToken(UserSignInRequest userSignInRequest);
}
