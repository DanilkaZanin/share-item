package com.example.demo.controller;

import com.example.demo.dto.response.FileResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.UserPhotoServiceSecurity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/users/files")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFileController {
    UserPhotoServiceSecurity userService;

    @PutMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse uploadFile(
            @RequestParam MultipartFile photo,
            @AuthenticationPrincipal UserInfoDetails user
    ) {
        return userService.uploadPhoto(user, photo);
    }

    @GetMapping
    public ResponseEntity<byte[]> downloadFile(@AuthenticationPrincipal UserInfoDetails user) {
        FileResponse fileResponse = userService.downloadPhoto(user);
        return ResponseEntity.ok()
                .headers(fileResponse.headers())
                .body(fileResponse.data());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@AuthenticationPrincipal UserInfoDetails user) {
        userService.deletePhoto(user);
    }
}
