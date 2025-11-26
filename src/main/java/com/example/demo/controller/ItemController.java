package com.example.demo.controller;

import com.example.demo.dto.request.ItemCreateRequest;
import com.example.demo.dto.request.ItemUpdateRequest;
import com.example.demo.dto.response.ItemResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.service.ItemService;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@RequestHeader("X-User-Id") Long userId,
                                   @RequestBody @Validated ItemCreateRequest request
    ) {
        return itemService.createItem(userId, request);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> getUserItems(@RequestHeader("X-User-Id") Long userId) {
        return itemService.getUserItems(userId);
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
    public MessageResponse deleteItem(@PathVariable Long itemId, @RequestHeader("X-User-Id") Long userId) {
        return itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse updateItem(
            @PathVariable Long itemId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ItemUpdateRequest request
    ) {
        return itemService.updateItem(userId, itemId, request);
    }
}
