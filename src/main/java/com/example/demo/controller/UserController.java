package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.dto.validation.ValidationGroups;
import com.example.demo.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody @Validated(ValidationGroups.Create.class) UserDto userCreateRequest
    ) {
        var userResponse = userService.createUser(userCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Validated(ValidationGroups.Update.class) UserDto userUpdateRequest
    ) {
        return ResponseEntity.ok(userService.updateUserProfile(id, userUpdateRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable @NotNull Long id) {
        var userResponse = userService.deleteUser(id);
        return ResponseEntity.ok(userResponse);
    }
}
