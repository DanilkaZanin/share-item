package com.example.demo.service.impl;

import com.example.demo.dto.request.UserUpdatePasswordRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.EntityService;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService, EntityService<User> {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return userMapper.toUserResponse(findUserById(userId));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserInfoDetails user, UserUpdateRequest userUpdateRequest) {
        User dbUser = userRepository.findUserByEmail(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException(user.getUsername()));

        userMapper.updateUser(userUpdateRequest, dbUser);
        return null;
    }

    @Override
    @Transactional
    public UserResponse updatePassword(UserInfoDetails user, UserUpdatePasswordRequest userUpdateRequest) {
        if (!passwordEncoder.matches(userUpdateRequest.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Bad credentials");
        } else if (passwordEncoder.matches(userUpdateRequest.newPassword(), user.getPassword())) {
            throw new RuntimeException("Bad credentials");
        }

        User dbUser = userRepository.findUserByEmail(user.getUsername()).orElseThrow(() -> new UserNotFoundException(user.getUsername()));
        dbUser.setPassword(passwordEncoder.encode(userUpdateRequest.newPassword()));
        return userMapper.toUserResponse(dbUser);
    }

    @Override
    public User getById(Long id) {
        return findUserById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
    }
}
