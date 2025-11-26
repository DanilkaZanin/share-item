package com.example.demo.service;

import com.example.demo.dto.request.ItemCreateRequest;
import com.example.demo.dto.request.ItemUpdateRequest;
import com.example.demo.dto.response.ItemResponse;
import com.example.demo.dto.response.MessageResponse;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(Long userId, ItemCreateRequest item);

    ItemResponse updateItem(Long userId, Long itemId, ItemUpdateRequest item);

    MessageResponse deleteItem(Long userId, Long itemId);

    ItemResponse getItem(Long id);

    List<ItemResponse> getUserItems(Long userId);

    List<ItemResponse> getItemsByName( String name);

    //todo сделать поиск elastic search
}
