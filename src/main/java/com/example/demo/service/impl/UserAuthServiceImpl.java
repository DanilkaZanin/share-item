package com.example.demo.service.impl;

import com.example.demo.dto.request.UserCreateRequest;
import com.example.demo.dto.request.UserSignInRequest;
import com.example.demo.dto.response.JwtAuthenticationResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ExistsMailException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.UserAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class UserAuthServiceImpl implements UserDetailsService, UserAuthService {
    UserRepository userRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new UserInfoDetails(user);
    }

    @Override
    public UserResponse saveUser(UserCreateRequest userCreateRequest) {
        log.info("Создание пользователя с мэйлом: {}", userCreateRequest.email());

        if (userRepository.existsByEmail(userCreateRequest.email())) {
            log.warn("Попытка создать пользователя с существующем мэйлом! {}", userCreateRequest.email());
            throw new ExistsMailException(userCreateRequest.email());
        }

        User user = userMapper.toUser(userCreateRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(Set.of(Role.USER));

        log.info("Статус и время регистрации установлено, сохранение пользователя {}", userCreateRequest.email());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public JwtAuthenticationResponse getJwtToken(UserSignInRequest userSignInRequest) {
        UserDetails user = loadUserByUsername(userSignInRequest.email());

        if (!passwordEncoder.matches(userSignInRequest.password(), user.getPassword())) {
            throw new RuntimeException("Bad credentials");
        }
        return new JwtAuthenticationResponse(jwtService.generateToken(user));
    }
}
