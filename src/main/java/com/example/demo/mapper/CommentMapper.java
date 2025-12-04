package com.example.demo.mapper;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CommentRequest comment);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booking.id", target = "bookingId")
    CommentResponse toResponse(Comment comment);
}
