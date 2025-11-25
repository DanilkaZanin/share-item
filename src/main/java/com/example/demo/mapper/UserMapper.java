package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUser(UserDto userDto, @MappingTarget User user);

    UserDto toUserDto(User user);
}

