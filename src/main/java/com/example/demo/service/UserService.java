package com.example.demo.service;


import com.example.demo.dto.UserDto;
import com.example.demo.dto.response.MessageResponse;


import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userCreateRequest);

    UserDto updateUserProfile(Long userId, UserDto userUpdateRequest);

    MessageResponse deleteUser(Long userId);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();
}
