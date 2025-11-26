package com.example.demo.repository;

import com.example.demo.dto.BookingResponse;
import com.example.demo.dto.response.BookingRequest;
import com.example.demo.entity.Booking;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<BookingRequest> getBookingsByItemId(Long itemId);

    List<BookingResponse> getBookingsByBooker_Id(Long bookerId);

    List<BookingResponse> getBookingsByBooker(User booker);

    List<BookingRequest> getBookingsByItemIdAndIdIsNot(Long itemId, Long id);

    List<BookingResponse> getBookingsByItem_Owner(User itemOwner);
}
