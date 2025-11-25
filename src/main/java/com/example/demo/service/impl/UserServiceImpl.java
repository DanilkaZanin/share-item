package com.example.demo.service.impl;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ExistsMailException;
import com.example.demo.exceptions.UserNotFoundException;
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
    public UserDto createUser(UserDto userCreateRequest) {
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
    public UserDto updateUserProfile(Long userId, UserDto userUpdateRequest) {
        User user = findUserById(userId);

        return userMapper.toUserDto(userRepository.save(userMapper.updateUser(userUpdateRequest, user)));
    }

    @Override
    public MessageResponse deleteUser(Long userId) {
        User user = findUserById(userId);

        userRepository.save(user);
        return new MessageResponse("User has been deleted");
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userMapper.toUserDto(findUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
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
