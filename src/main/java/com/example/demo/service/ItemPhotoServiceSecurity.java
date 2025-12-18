package com.example.demo.service;

import com.example.demo.dto.response.FileResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.security.UserInfoDetails;
import org.springframework.web.multipart.MultipartFile;

public interface ItemPhotoServiceSecurity {
    MessageResponse uploadPhoto(Long itemId, UserInfoDetails user, MultipartFile photo);
    FileResponse downloadPhoto(Long itemId, UserInfoDetails user);
    void deletePhoto(Long itemId, UserInfoDetails user);
}
