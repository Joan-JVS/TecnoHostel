package com.tecnocampus.tecnohostal.application.dto;

import java.time.LocalDate;

/** Body de POST /bookings. Les dates van en format ISO: "2026-12-01". */
public record CreateBookingDTO(String guestId, String roomId, LocalDate checkIn, LocalDate checkOut) {}
