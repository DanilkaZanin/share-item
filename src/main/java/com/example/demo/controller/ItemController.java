package com.example.demo.controller;

import com.example.demo.dto.request.ItemCreateRequest;
import com.example.demo.dto.request.ItemUpdateRequest;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.dto.response.ItemResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.CommentService;
import com.example.demo.service.ItemService;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    ItemService itemService;
    CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(
            @AuthenticationPrincipal UserInfoDetails user,
            @RequestBody @Validated ItemCreateRequest request
    ) {
        return itemService.createItem(user.getId(), request);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> getUserItems(@AuthenticationPrincipal UserInfoDetails user) {
        return itemService.getUserItems(user.getId());
    }

    @GetMapping("/search/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> getItemsByName(
            @PathVariable @NotBlank String name
    ) {
        return itemService.getItemsByName(name);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserInfoDetails user
    ) {
        return itemService.deleteItem(user.getId(), itemId);
    }

    @PutMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse updateItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserInfoDetails user,
            @RequestBody ItemUpdateRequest request
    ) {
        return itemService.updateItem(user.getId(), itemId, request);
    }

    @GetMapping("/{itemId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getComments(@PathVariable Long itemId) {
        return commentService.getComments(itemId);
    }
}
