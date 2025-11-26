package com.example.demo.mapper;

import com.example.demo.dto.response.ItemResponse;
import com.example.demo.dto.request.ItemCreateRequest;
import com.example.demo.dto.request.ItemUpdateRequest;
import com.example.demo.entity.Item;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ItemMapper {


    ItemResponse toItemResponse(Item item);


    Item toItem(ItemCreateRequest itemCreateRequest);
    Item toItem(ItemUpdateRequest itemUpdateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item updateItem(ItemUpdateRequest itemUpdateRequest, @MappingTarget Item item);
}
