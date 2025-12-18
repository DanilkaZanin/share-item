package com.example.demo.service;

import com.example.demo.dto.response.FileResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.security.UserInfoDetails;
import org.springframework.web.multipart.MultipartFile;

public interface UserPhotoServiceSecurity {
    MessageResponse uploadPhoto(UserInfoDetails user, MultipartFile photo);
    FileResponse downloadPhoto(UserInfoDetails user);
    void deletePhoto(UserInfoDetails user);
}
