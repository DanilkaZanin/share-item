package com.example.demo.service.impl;

import com.example.demo.dto.ItemDto;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.entity.Item;
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
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        checkUserService.getById(userId);

        var item = itemMapper.toItem(itemDto);
        item.setCreatedAt(LocalDateTime.now());

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        checkUserService.getById(userId);

        var item = itemMapper.updateItem(itemDto, findItemById(itemId));

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public MessageResponse deleteItem(Long userId, Long itemId) {
        checkUserService.getById(userId);

        itemRepository.deleteById(itemId);
        return new MessageResponse("Item deleted successfully");
    }

    @Override
    public ItemDto getItem(Long id) {
        return itemMapper.toItemDto(findItemById(id));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        checkUserService.getById(userId);

        List<Item> items = itemRepository.getItemsByOwnerId(userId);
        return items.stream().map(itemMapper::toItemDto).toList();
    }


    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item with id : " + itemId + " not found"));
    }

    @Override
    public Item getById(Long id) {
        return null;
    }
}
