package com.example.demo.repository;

import com.example.demo.dto.response.ItemResponse;
import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item getItemById(Long id);

    List<Item> getItemsByOwnerId(Long ownerId);

    List<ItemResponse> getItemsByNameContaining(String name);
}
