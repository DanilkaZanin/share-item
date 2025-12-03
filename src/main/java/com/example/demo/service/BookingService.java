package com.example.demo.service;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.Status;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(Long userId, BookingRequest bookingRequest);

    BookingResponse updateStatus(Long userId, Long bookingId, Status newStatus);

    BookingResponse updateBookingDates(Long userId, @NotNull Long bookingId, BookingRequest bookingRequest);

    void deleteBooking(Long userId, @NotNull Long bookingId);

    BookingResponse getBooking(Long userId, @NotNull Long bookingId);

    List<BookingResponse> getUserBookings(Long userId);

    List<BookingResponse> getOwnersBookings(Long userId);
}
