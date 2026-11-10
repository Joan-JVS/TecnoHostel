package com.tecnocampus.tecnohostal.application.dto;

import com.tecnocampus.tecnohostal.domain.Room;

public record RoomDTO(
        String id,
        String roomNumber,
        String type,
        double pricePerNight,
        String status,
        int condition
) {
    public static RoomDTO from(Room room) {
        return new RoomDTO(room.getId(), room.getRoomNumber(), room.getType().name(),
                room.getPricePerNight(), room.getStatus().name(), room.getCondition());
    }
}
