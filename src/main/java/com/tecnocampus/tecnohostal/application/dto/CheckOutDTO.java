package com.tecnocampus.tecnohostal.application.dto;

/** Body OPCIONAL de POST /bookings/{id}/check-out (Ticket #3). roomCondition va de 0 a 100. */
public record CheckOutDTO(Integer roomCondition) {}
