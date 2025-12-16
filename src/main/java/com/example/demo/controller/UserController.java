package com.example.demo.controller;

import com.example.demo.dto.request.UserUpdatePasswordRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(
            @RequestBody @Validated UserUpdateRequest userUpdateRequest,
            @AuthenticationPrincipal UserInfoDetails user
    ) {
        return userService.updateUser(user, userUpdateRequest);
    }

    @PutMapping("/update/password")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updatePassword(
            @RequestBody @Validated UserUpdatePasswordRequest userUpdatePasswordRequest,
            @AuthenticationPrincipal UserInfoDetails user
    ) {
        return userService.updatePassword(user, userUpdatePasswordRequest);
    }

    // todo сделать обновление мэйла, при этом необходима генерация нового токена

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(@PathVariable @NotNull Long id) {
        userService.deleteUser(id);
    }
}
