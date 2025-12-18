package com.example.demo.controller;


import com.example.demo.dto.response.FileResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.ItemPhotoServiceSecurity;
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
@RequestMapping("/items/{itemId}/files")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ItemFileController {
    ItemPhotoServiceSecurity itemService;

    @PutMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse uploadFile(
            @PathVariable(name = "itemId") Long itemId,
            @RequestParam MultipartFile photo,
            @AuthenticationPrincipal UserInfoDetails user
    ) {
        return itemService.uploadPhoto(itemId, user, photo);
    }

    @GetMapping
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable(name = "itemId") Long itemId,
            @AuthenticationPrincipal UserInfoDetails user
    ) {
        FileResponse fileResponse = itemService.downloadPhoto(itemId, user);
        return ResponseEntity.ok()
                .headers(fileResponse.headers())
                .body(fileResponse.data());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(
            @PathVariable(name = "itemId") Long itemId,
            @AuthenticationPrincipal UserInfoDetails user
    ) {
        itemService.deletePhoto(itemId, user);
    }
}
