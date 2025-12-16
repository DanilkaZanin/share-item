package com.example.demo.service;


import com.example.demo.dto.request.UserUpdatePasswordRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.security.UserInfoDetails;

import java.util.List;

public interface UserService {
    void deleteUser(Long userId);

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UserInfoDetails user, UserUpdateRequest userUpdateRequest);

    UserResponse updatePassword(UserInfoDetails user, UserUpdatePasswordRequest userUpdateRequest);
}
