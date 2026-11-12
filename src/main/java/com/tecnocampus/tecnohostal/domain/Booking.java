package com.tecnocampus.tecnohostal.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Una reserva. Guarda els objectes reals Guest i Room (no només els ids): el BookingRepository
 * llegeix guest_id i room_id de la fila i carrega el client i l'habitació corresponents.
 */
public class Booking {

    private String id = UUID.randomUUID().toString();
    private Guest guest;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BookingStatus status;
    private double totalPrice;

    /** Reserva nova: sempre comença CONFIRMED. */
    public Booking(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, double totalPrice) {
        if (guest == null) {
            throw new IllegalArgumentException("Booking guest is required");
        }
        if (room == null) {
            throw new IllegalArgumentException("Booking room is required");
        }
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
        validateTotalPrice(totalPrice);
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = BookingStatus.CONFIRMED;
        this.totalPrice = totalPrice;
    }

    /** Reconstrueix una reserva de la base de dades, amb el client i l'habitació ja carregats. */
    public Booking(String id, Guest guest, Room room, LocalDate checkIn, LocalDate checkOut,
                   BookingStatus status, double totalPrice) {
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    private void validateTotalPrice(double totalPrice) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("Booking total price cannot be negative");
        }
    }

    /** Nombre de nits de l'estada (del dia d'entrada al dia de sortida). */
    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public String getId() {
        return id;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setStatus(BookingStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Booking status is required");
        }
        this.status = status;
    }

    public void setTotalPrice(double totalPrice) {
        validateTotalPrice(totalPrice);
        this.totalPrice = totalPrice;
    }

    public void applyCheckoutPenalty(Integer roomCondition) {
        if (roomCondition < 30){
            setTotalPrice(
                    getTotalPrice() + room.getPricePerNight()
            );
        }
    }
}
