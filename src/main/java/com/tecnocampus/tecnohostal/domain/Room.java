package com.tecnocampus.tecnohostal.domain;

import java.util.UUID;

/**
 * Una habitació de l'hostal.
 * <p>
 * Té dos constructors: un per crear una habitació NOVA (valida les dades i genera l'id) i un
 * altre per RECONSTRUIR una habitació que ja és a la base de dades (el fa servir el RowMapper
 * del RoomRepository).
 */
public class Room {

    private String id = UUID.randomUUID().toString();
    private String roomNumber;
    private RoomType type;
    private double pricePerNight;
    private RoomStatus status;
    private int condition;

    /** Habitació nova: comença AVAILABLE i en perfecte estat (100). */
    public Room(String roomNumber, RoomType type, double pricePerNight) {
        validateRoomNumber(roomNumber);
        validateType(type);
        validatePricePerNight(pricePerNight);
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.status = RoomStatus.AVAILABLE;
        this.condition = 100;
    }

    /** Reconstrueix una habitació a partir d'una fila de la taula rooms. */
    public Room(String id, String roomNumber, RoomType type, double pricePerNight,
                RoomStatus status, int condition) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.condition = condition;
    }

    private void validateRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("Room number is required");
        }
    }

    private void validateType(RoomType type) {
        if (type == null) {
            throw new IllegalArgumentException("Room type is required");
        }
    }

    private void validatePricePerNight(double pricePerNight) {
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Room price per night must be positive");
        }
    }

    public String getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public int getCondition() {
        return condition;
    }

    public void setStatus(RoomStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Room status is required");
        }
        this.status = status;
    }

    /** L'estat físic de l'habitació va de 0 (destrossada) a 100 (perfecta). */
    public void setCondition(int condition) {
        if (condition < 0 || condition > 100) {
            throw new IllegalArgumentException("Room condition must be between 0 and 100");
        }
        this.condition = condition;
    }
}
