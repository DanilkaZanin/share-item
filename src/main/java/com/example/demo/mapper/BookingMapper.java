package com.example.demo.mapper;

import com.example.demo.dto.BookingResponse;
import com.example.demo.dto.response.BookingRequest;
import com.example.demo.entity.Booking;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface BookingMapper {

    Booking toBooking(BookingRequest bookingRequest);
    BookingRequest toRequest(Booking booking);
    BookingResponse toResponse(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking updateBooking(BookingRequest bookingRequest, @MappingTarget Booking booking);
}
