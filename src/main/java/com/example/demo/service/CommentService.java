package com.example.demo.service;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(Long bookerId, CommentRequest commentRequest);

    void deleteComment(Long commentId, Long userId);

    List<CommentResponse> getComments(Long itemId);
}
