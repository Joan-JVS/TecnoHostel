package com.tecnocampus.tecnohostal.application.dto;

/**
 * Una fila de l'informe GET /reports/revenue (Ticket #6): una per cada tipus d'habitació.
 * Els noms dels camps són els que llegeixen els tests: no els canviïs.
 */
public record RoomTypeRevenueDTO(
        String roomType,
        int totalRooms,
        int checkedOutBookings,
        double totalRevenue,
        double averageBookingPrice
) {}
