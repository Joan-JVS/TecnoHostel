package com.tecnocampus.tecnohostal.application.dto;

import java.util.List;

/**
 * Resposta de GET /guests/{id}/history (Ticket #5).
 * Els noms dels camps són els que llegeixen els tests: no els canviïs.
 */
public record GuestHistoryDTO(
        GuestDTO guest,
        double totalSpent,
        int totalStays,
        List<BookingDTO> bookings
) {}
