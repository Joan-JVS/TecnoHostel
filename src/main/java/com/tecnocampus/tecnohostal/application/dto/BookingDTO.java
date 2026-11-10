package com.tecnocampus.tecnohostal.application.dto;

import com.tecnocampus.tecnohostal.domain.Booking;

public record BookingDTO(
        String id,
        String guestId,
        String guestName,
        String roomId,
        String roomNumber,
        String checkIn,
        String checkOut,
        String status,
        double totalPrice
) {
    public static BookingDTO from(Booking b) {
        return new BookingDTO(b.getId(), b.getGuest().getId(), b.getGuest().getName(),
                b.getRoom().getId(), b.getRoom().getRoomNumber(),
                b.getCheckIn().toString(), b.getCheckOut().toString(),
                b.getStatus().name(), b.getTotalPrice());
    }
}
