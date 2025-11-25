package com.example.demo.controller;

import com.example.demo.dto.ItemDto;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.dto.validation.ValidationGroups;
import com.example.demo.service.ItemService;
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
    public ItemDto createItem(@RequestHeader("X-User-Id") Long userId,
                              @RequestBody @Validated(ValidationGroups.Create.class) ItemDto request
    ) {
        return itemService.createItem(userId, request);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getUserItems(@RequestHeader("X-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse deleteItem(@PathVariable Long itemId, @RequestHeader("X-User-Id") Long userId) {
        return itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Validated(ValidationGroups.Update.class) ItemDto request
    ) {
        return itemService.updateItem(userId, itemId, request);
    }
}
