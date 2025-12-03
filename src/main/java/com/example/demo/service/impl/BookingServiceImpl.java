package com.example.demo.service.impl;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Item;
import com.example.demo.entity.Status;
import com.example.demo.entity.User;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.BookingNotFoundException;
import com.example.demo.exception.DateConflictException;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.repository.BookingRepository;
import com.example.demo.service.BookingService;
import com.example.demo.service.EntityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;

    EntityService<Item> checkItemService;
    EntityService<User> checkUserService;

    BookingMapper bookingMapper;

    @Override
    public BookingResponse createBooking(Long userId, BookingRequest bookingRequest) {
        var user = checkUserService.getById(userId);
        var item = checkItemService.getById(bookingRequest.itemId());

        checkOverlapping(Optional.empty(), bookingRequest.itemId(), bookingRequest);

        var booking = bookingMapper.toBooking(bookingRequest);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse updateStatus(Long userId, Long bookingId, Status newStatus) {
        var user = checkUserService.getById(userId);
        var booking = getBooking(bookingId);

        checkOwnerAccess(user, booking);

        booking.setStatus(newStatus);
        booking.setVerifiedAt(LocalDateTime.now());
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse updateBookingDates(Long userId, Long bookingId, BookingRequest bookingRequest) {
        var user = checkUserService.getById(userId);
        var booking = getBooking(bookingId);

        checkBookerAccess(user, booking);

        booking.setStartDate(bookingRequest.startDate());
        booking.setEndDate(bookingRequest.endDate());

        //после изменения объекта сбрасывается его статус и время верификации
        booking.setVerifiedAt(null);
        booking.setStatus(Status.WAITING);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public void deleteBooking(Long userId, Long bookingId) {
        var user = checkUserService.getById(userId);
        var booking = getBooking(bookingId);

        checkBookerOrOwnerAccess(user, booking);

        bookingRepository.delete(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long userId, Long bookingId) {
        var user = checkUserService.getById(userId);
        var booking = getBooking(bookingId);

        //нужно как-то подгружать id юзера и овнера
        checkBookerOrOwnerAccess(user, booking);

        return bookingMapper.toResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(Long userId) {
        var user = checkUserService.getById(userId);
        return bookingRepository.getBookingsByBooker(user);
    }

    @Override
    public List<BookingResponse> getOwnersBookings(Long userId) {
        var user = checkUserService.getById(userId);
        return bookingRepository.getBookingsByItem_Owner(user);
    }

    //проверка что юзер, это человек который бронирует
    private void checkBookerAccess(User user, Booking booking) {
        if (!booking.getBooker().getId().equals(user.getId()))
            throw new AccessDeniedException("Access denied, user with id %s is not booker".formatted(user.getId()));
    }

    private void checkOwnerAccess(User user, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Access denied, user with id %s is not owner".formatted(user.getId()));
    }

    private void checkBookerOrOwnerAccess(User user, Booking booking) {
        boolean isBooker = booking.getBooker().getId().equals(user.getId());
        boolean isOwner = booking.getItem().getOwner().getId().equals(user.getId());

        if (!isBooker && !isOwner)
            throw new AccessDeniedException("Access denied, user with id %s is not owner or booker".formatted(user.getId()));
    }

    private void checkOverlapping(Optional<Long> bookingId, Long itemId, BookingRequest bookingRequest) {
        boolean isOverlapping = bookingId
                .map(id -> getItemBookingsExceptCurrent(itemId, id).stream()
                        .anyMatch(booking -> isOverlapping(bookingRequest, booking)))
                .orElse(getItemBookings(itemId).stream()
                        .anyMatch(booking -> isOverlapping(bookingRequest, booking)));

        if (isOverlapping)
            throw new DateConflictException("Selected dates are not available");
    }

    private boolean isOverlapping(BookingRequest newBooking, BookingRequest existing) {
        return newBooking.startDate().isBefore(existing.endDate())
                && newBooking.endDate().isAfter(existing.startDate());
    }

    private List<BookingRequest> getItemBookings(Long itemId) {
        return bookingRepository.getBookingsByItemId(itemId);
    }

    private List<BookingRequest> getItemBookingsExceptCurrent(Long itemId, Long bookingId) {
        return bookingRepository.getBookingsByItemIdAndIdIsNot(itemId, bookingId);
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException("Booking with id %s not found".formatted(id))
        );
    }
}
