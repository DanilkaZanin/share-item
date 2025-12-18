package com.example.demo.service;

import com.example.demo.dto.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    /**
     * entity Key - ключ, который будет содержаться в сигнатуре файла s3 хранилища
     * */
    String uploadPhoto(MultipartFile photo, String entityKey);

    FileResponse downloadPhoto(String photoKey);

    void deletePhoto(String photoKey);
}
