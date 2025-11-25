package com.example.demo.mapper;

import com.example.demo.dto.ItemDto;
import com.example.demo.entity.Item;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ItemMapper {


    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item updateItem(ItemDto itemDto, @MappingTarget Item item);
}
