package com.example.demo.service.impl;

import com.example.demo.dto.request.UserCreateRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ExistsMailException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EntityService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, EntityService<User> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * При добавлении пользователя вписываем статус активности и дату время создания
     */
    @Override
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        log.info("Создание пользователя с мэйлом: {}", userCreateRequest.email());

        if (userRepository.existsByEmail(userCreateRequest.email())) {
            log.warn("Попытка создать пользователя с существующем мэйлом! {}", userCreateRequest.email());
            throw new ExistsMailException(userCreateRequest.email());
        }

        User user = userMapper.toUser(userCreateRequest);
        user.setCreatedAt(LocalDateTime.now());

        log.info("Статус и время регистрации установлено, сохранение пользователя {}", userCreateRequest.email());
        return userMapper.toUserDto(userRepository.save(user));
    }

    /**
     * добавление дополнительных данных
     */
    @Override
    public UserResponse updateUserProfile(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = findUserById(userId);

        return userMapper.toUserDto(userRepository.save(userMapper.updateUser(userUpdateRequest, user)));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return userMapper.toUserDto(findUserById(userId));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public User getById(Long id) {
        return findUserById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
    }
}
