package com.example.demo.service.impl;

import com.example.demo.dto.request.UserUpdatePasswordRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.FileResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoudPhotoException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserPhotoExistException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.message.MessageHelper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.EntityService;
import com.example.demo.service.PhotoService;
import com.example.demo.service.UserPhotoServiceSecurity;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService, EntityService<User>, UserPhotoServiceSecurity {
    UserRepository userRepository;

    UserMapper userMapper;

    MessageHelper messageHelper;

    PasswordEncoder passwordEncoder;

    @Qualifier("userPhotoService")
    PhotoService userPhotoService;

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

        User dbUser = findUserByEmail(user.getUsername());
        dbUser.setPassword(passwordEncoder.encode(userUpdateRequest.newPassword()));
        return userMapper.toUserResponse(dbUser);
    }

    @Override
    public User getById(Long id) {
        return findUserById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                messageHelper.get("user.not.found.exception", id))
        );
    }

    private User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    @Transactional
    public MessageResponse uploadPhoto(UserInfoDetails user, MultipartFile photo) {
        var dbUser = findUserByEmail(user.getUsername());


        if (dbUser.getPhotoKey() != null)
            throw new UserPhotoExistException(messageHelper.get("user.photo.already.exists"));
        String photoKey = userPhotoService.uploadPhoto(photo, user.getUsername());
        dbUser.setPhotoKey(photoKey);
        return new MessageResponse("photo uploaded successfully");
    }

    @Override
    public FileResponse downloadPhoto(UserInfoDetails user) {
        var dbUser = findUserByEmail(user.getUsername());
        return userPhotoService.downloadPhoto(dbUser.getPhotoKey());
    }

    @Override
    public void deletePhoto(UserInfoDetails user) {
        var dbUser = findUserByEmail(user.getUsername());
        if (dbUser.getPhotoKey() == null)
            throw new UserNotFoudPhotoException(messageHelper.get("user.photo.not.found.exception"));
        userPhotoService.deletePhoto(dbUser.getPhotoKey());
    }
}
