package com.example.demo.service.impl;

import com.example.demo.dto.request.ItemCreateRequest;
import com.example.demo.dto.request.ItemUpdateRequest;
import com.example.demo.dto.response.FileResponse;
import com.example.demo.dto.response.ItemResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.entity.Item;
import com.example.demo.entity.ItemStatus;
import com.example.demo.entity.User;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.exception.ItemPhotoExistException;
import com.example.demo.exception.ItemPhotoNotExistException;
import com.example.demo.exception.UserNotOwnerException;
import com.example.demo.mapper.ItemMapper;
import com.example.demo.message.MessageHelper;
import com.example.demo.repository.ItemRepository;
import com.example.demo.security.UserInfoDetails;
import com.example.demo.service.EntityService;
import com.example.demo.service.ItemPhotoServiceSecurity;
import com.example.demo.service.ItemService;
import com.example.demo.service.PhotoService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService, EntityService<Item>, ItemPhotoServiceSecurity {
    ItemRepository itemRepository;

    EntityService<User> checkUserService;

    ItemMapper itemMapper;

    MessageHelper messageHelper;

    @Qualifier("itemPhotoService")
    PhotoService itemPhotoService;

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
        return new MessageResponse(messageHelper.get("item.deleted"));
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
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                messageHelper.get("item.not.found.exception", itemId))
        );
    }

    @Override
    public Item getById(Long id) {
        return findItemById(id);
    }

    @Override
    @Transactional
    public MessageResponse uploadPhoto(Long itemId, UserInfoDetails user, MultipartFile photo) {
        var dbItem = findItemById(itemId);

        checkUserIsOwner(dbItem, user);

        if (dbItem.getPhotoKey() != null)
            throw new ItemPhotoExistException(messageHelper.get("item.photo.already.exists"));
        var key = itemPhotoService.uploadPhoto(photo, itemId.toString());

        dbItem.setPhotoKey(key);
        return new MessageResponse("photo uploaded successfully");
    }

    @Override
    public FileResponse downloadPhoto(Long itemId, UserInfoDetails user) {
        var dbItem = findItemById(itemId);

        checkUserIsOwner(dbItem, user);

        if(dbItem.getPhotoKey() == null)
            throw new ItemPhotoNotExistException(messageHelper.get("item.photo.not.exists"));
        return itemPhotoService.downloadPhoto(dbItem.getPhotoKey());
    }

    @Override
    @Transactional
    public void deletePhoto(Long itemId, UserInfoDetails user) {
        var dbItem = findItemById(itemId);

        checkUserIsOwner(dbItem, user);

        itemPhotoService.deletePhoto(dbItem.getPhotoKey());
        dbItem.setPhotoKey(null);
    }

    private void checkUserIsOwner(Item item, UserInfoDetails user) {
        if(!Objects.equals(item.getOwner().getEmail(), user.getUsername())) {
            throw new UserNotOwnerException(messageHelper.get("user.not.owner.exception",  user.getUsername(), item.getId()));
        }
    }
}
