package com.example.demo.service.impl;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.exception.BookingNotThisItemException;
import com.example.demo.exception.CommentNotFoudException;
import com.example.demo.exception.UserIsNotBookerException;
import com.example.demo.exception.UserIsNotCommentatorException;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.message.MessageHelper;
import com.example.demo.repository.CommentRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.EntityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;

    CommentMapper commentMapper;
    MessageHelper messageHelper;

    EntityService<Item> checkItemService;
    EntityService<User> checkUserService;
    EntityService<Booking> checkBookingService;

    @Override
    @Transactional
    public CommentResponse addComment(Long bookerId, CommentRequest commentRequest) {
        var item = checkItemService.getById(commentRequest.itemId());
        var user = checkUserService.getById(bookerId);
        var booking = checkBookingService.getById(commentRequest.bookingId());

        checkUserBookedItem(item, user, booking);

        var comment = commentMapper.toComment(commentRequest);
        comment.setBooking(booking);
        comment.setItem(item);
        comment.setUser(user);

        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        var comment = getComment(commentId);
        var user = checkUserService.getById(userId);

        checkUserDoComment(user, comment);

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentResponse> getComments(Long itemId) {
        checkItemService.getById(itemId);
        return commentRepository.getCommentsByItem_Id(itemId);
    }

    private void checkUserBookedItem(Item item, User user, Booking booking) {
        if (!user.getId().equals(booking.getBooker().getId()))
            throw new UserIsNotBookerException(
                    messageHelper.get("comment.user.is.not.booker.exception",user.getId(), booking.getId())
            );

        if (!item.getId().equals(booking.getItem().getId()))
            throw new BookingNotThisItemException(
                    messageHelper.get("comment.item.is.not.in.booking.exception", item.getId(), booking.getId())
            );
    }

    private void checkUserDoComment(User user, Comment comment) {
        if (!user.getId().equals(comment.getUser().getId()))
            throw new UserIsNotCommentatorException(
                    messageHelper.get("comment.user.is.not.author.exception", user.getId(), comment.getId())
            );
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoudException(
                        messageHelper.get("comment.not.found.exception", commentId))
                );
    }
}
