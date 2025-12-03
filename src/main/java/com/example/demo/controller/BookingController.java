package com.example.demo.controller;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.UpdateStatusBookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.service.BookingService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * В букингах мы должны
 * 1) создавать запрос на бронирование
 * 2) подтверждать/отклонять бронирование
 * 3) изменять даты бронирования
 * 4) забирать все бронирования вещи
 * 6) забирать конкретную бронь
 */
@RestController
@RequestMapping("bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @RequestHeader("X-User-Id") @NotNull Long userId,
            @RequestBody @Validated BookingRequest bookingRequest
    ) {
        return bookingService.createBooking(userId, bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse updateBookingDates(
            @RequestHeader("X-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestBody @Validated BookingRequest bookingRequest
    ) {
        return bookingService.updateBookingDates(userId, bookingId, bookingRequest);
    }

    @PatchMapping("/{bookingId}/status")
    public BookingResponse updateBookingStatus(
            @RequestHeader("X-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestBody UpdateStatusBookingRequest status
    ) {
        return bookingService.updateStatus(userId, bookingId, status.status());
    }

    @DeleteMapping("{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBooking(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable @NotNull Long bookingId
    ) {
        bookingService.deleteBooking(userId, bookingId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getBooking(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable @NotNull Long bookingId
    ) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getUserBookings(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/owners")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getOwnersBooking(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return bookingService.getOwnersBookings(userId);
    }

    // букинги, которые нуждаются в вашем подтверждении
    @GetMapping("/owners/wait-to-werify")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getOwnersBookings(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return null;
    }
}
