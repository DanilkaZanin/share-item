package com.example.demo.repository;

import com.example.demo.dto.response.CommentResponse;
import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentResponse> getCommentsByItem_Id(Long itemId);
}
