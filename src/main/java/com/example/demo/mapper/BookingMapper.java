package com.example.demo.mapper;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.Booking;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingRequest bookingRequest);
    BookingRequest toRequest(Booking booking);

    @Mapping(target = "itemId", source ="item.id")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingResponse toResponse(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking updateBooking(BookingRequest bookingRequest, @MappingTarget Booking booking);
}
