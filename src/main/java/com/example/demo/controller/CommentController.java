package com.example.demo.controller;


import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.service.CommentService;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * создание комментария
 * удаление комментария
 **/
@RestController
@Validated
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @RequestBody @Validated CommentRequest comment,
            @RequestHeader("X-User-Id") Long bookerId
    ) {
        return commentService.addComment(bookerId, comment);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(
            @PathVariable @NotNull Long commentId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        commentService.deleteComment(commentId, userId);
    }
}
