package com.example.demo.service;

import com.example.demo.dto.ItemDto;
import com.example.demo.dto.response.MessageResponse;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    MessageResponse deleteItem(Long userId, Long itemId);

    ItemDto getItem(Long id);

    List<ItemDto> getUserItems(Long userId);

    //todo сделать поиск elastic search
}
