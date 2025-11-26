package com.example.demo.service.impl;

import com.example.demo.dto.request.ItemCreateRequest;
import com.example.demo.dto.request.ItemUpdateRequest;
import com.example.demo.dto.response.ItemResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.entity.Item;
import com.example.demo.entity.ItemStatus;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.mapper.ItemMapper;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.EntityService;
import com.example.demo.service.ItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService, EntityService<Item> {
    ItemRepository itemRepository;

    EntityService<User> checkUserService;

    ItemMapper itemMapper;

    @Override
    public ItemResponse createItem(Long userId, ItemCreateRequest itemDto) {
        var user = checkUserService.getById(userId);
        var item = itemMapper.toItem(itemDto);

        item.setCreatedAt(LocalDateTime.now());
        item.setStatus(ItemStatus.AVAILABLE);
        item.setOwner(user);

        return itemMapper.toItemResponse(itemRepository.save(item));
    }

    @Override
    public ItemResponse updateItem(Long userId, Long itemId, ItemUpdateRequest itemDto) {
        checkUserService.getById(userId);

        var item = itemMapper.updateItem(itemDto, findItemById(itemId));

        return itemMapper.toItemResponse(itemRepository.save(item));
    }

    @Override
    public MessageResponse deleteItem(Long userId, Long itemId) {
        checkUserService.getById(userId);

        itemRepository.deleteById(itemId);
        return new MessageResponse("Item deleted successfully");
    }

    @Override
    public ItemResponse getItem(Long id) {
        return itemMapper.toItemResponse(findItemById(id));
    }

    @Override
    public List<ItemResponse> getUserItems(Long userId) {
        checkUserService.getById(userId);

        List<Item> items = itemRepository.getItemsByOwnerId(userId);
        return items.stream().map(itemMapper::toItemResponse).toList();
    }

    @Override
    public List<ItemResponse> getItemsByName(String name) {
        return itemRepository.getItemsByNameContaining(name);
    }


    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item with id : " + itemId + " not found"));
    }

    @Override
    public Item getById(Long id) {
        return null;
    }
}
