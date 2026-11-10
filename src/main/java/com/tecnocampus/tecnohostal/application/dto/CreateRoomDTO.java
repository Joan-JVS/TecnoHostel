package com.tecnocampus.tecnohostal.application.dto;

/** Body de POST /rooms. type: SINGLE, DOUBLE o SUITE. */
public record CreateRoomDTO(String roomNumber, String type, double pricePerNight) {}
