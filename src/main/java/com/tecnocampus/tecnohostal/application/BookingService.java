package com.tecnocampus.tecnohostal.application;

import com.tecnocampus.tecnohostal.application.dto.BookingDTO;
import com.tecnocampus.tecnohostal.application.dto.CreateBookingDTO;
import com.tecnocampus.tecnohostal.domain.Booking;
import com.tecnocampus.tecnohostal.domain.BookingStatus;
import com.tecnocampus.tecnohostal.domain.Guest;
import com.tecnocampus.tecnohostal.domain.Room;
import com.tecnocampus.tecnohostal.domain.RoomStatus;
import com.tecnocampus.tecnohostal.exception.BadRequestException;
import com.tecnocampus.tecnohostal.exception.NotFoundException;
import com.tecnocampus.tecnohostal.persistence.BookingRepository;
import com.tecnocampus.tecnohostal.persistence.GuestRepository;
import com.tecnocampus.tecnohostal.persistence.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository,
                          GuestRepository guestRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream().map(BookingDTO::from).toList();
    }

    public BookingDTO getBookingById(String id) {
        return BookingDTO.from(findBooking(id));
    }

    public BookingDTO createBooking(CreateBookingDTO dto) {
        Guest guest = findGuest(dto.guestId());
        Room room = findRoom(dto.roomId());
        requireAvailable(room);
        requireValidDateRange(dto.checkIn(), dto.checkOut());

        // TODO Ticket #4: aquí no es comprova si l'habitació ja està reservada per a aquestes dates.
        //  Dues estades se solapen quan existingCheckIn < newCheckOut I existingCheckOut > newCheckIn.
        //  Només bloquegen les reserves CONFIRMED i CHECKED_IN de la mateixa habitació.
        //  Si n'hi ha alguna: BadRequestException("Room is already booked for the requested dates").
        boolean overlappingBooking = bookingRepository.findAll().stream()
                .filter(b -> b.getRoom().getId().equals(room.getId()))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED
                || b.getStatus() == BookingStatus.CHECKED_IN)
                .anyMatch(b -> b.getCheckIn().isBefore(dto.checkOut())
                && b.getCheckOut().isAfter(dto.checkIn()));

        if (overlappingBooking){
            throw new BadRequestException("Room is already booked for the requested dates");
        }

        long nights = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        // TODO Ticket #1: no s'aplica el descompte VIP del client (STANDARD 0%, SILVER 10%, GOLD 15%).
        double discount = switch (guest.getVipLevel()){
            case STANDARD -> 0.0;
            case SILVER -> 0.10;
            case GOLD -> 0.15;
        };

        double totalPrice = nights * room.getPricePerNight() * (1 - discount);

        Booking booking = new Booking(guest, room, dto.checkIn(), dto.checkOut(), totalPrice);
        bookingRepository.save(booking);
        return BookingDTO.from(booking);
    }

    public BookingDTO checkIn(String id) {
        Booking booking = findBooking(id);
        requireStatus(booking, BookingStatus.CONFIRMED, "Only confirmed bookings can be checked in");

        Room room = booking.getRoom();
        booking.setStatus(BookingStatus.CHECKED_IN);
        room.setStatus(RoomStatus.OCCUPIED);

        bookingRepository.update(booking);
        roomRepository.update(room);
        return BookingDTO.from(booking);
    }

    public BookingDTO checkOut(String id, Integer roomCondition) {
        Booking booking = findBooking(id);
        requireStatus(booking, BookingStatus.CHECKED_IN, "Only checked-in bookings can be checked out");

        Room room = booking.getRoom();
        booking.setStatus(BookingStatus.CHECKED_OUT);
        /*room.setStatus(RoomStatus.AVAILABLE);*/

        // TODO Ticket #3: roomCondition arriba del body {"roomCondition": N}, però no es fa servir.
        //  - Si és null (check-out sense body), tot ha de quedar com ara.
        //  - Guarda-la a l'habitació (room.setCondition ja valida que vagi de 0 a 100).
        //  - N < 50: l'habitació passa a MAINTENANCE en lloc d'AVAILABLE.
        //  - N < 30: a més, suma una nit (el preu per nit de l'habitació) al totalPrice de la reserva.
        //  Els dos update(...) de sota ja guarden la reserva i l'habitació amb tots els seus camps.
        if (roomCondition != null){
            room.setCondition(roomCondition);

            if (roomCondition < 50){
                room.setStatus(RoomStatus.MAINTENANCE);
            }else {
                room.setStatus(RoomStatus.AVAILABLE);
            }

            if (roomCondition < 30){
                booking.setTotalPrice(
                        booking.getTotalPrice() + room.getPricePerNight()
                );
            }
        }else {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        bookingRepository.update(booking);
        roomRepository.update(room);
        return BookingDTO.from(booking);
    }

    public BookingDTO cancelBooking(String id) {
        Booking booking = findBooking(id);
        requireStatus(booking, BookingStatus.CONFIRMED, "Only confirmed bookings can be cancelled");

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.update(booking);
        return BookingDTO.from(booking);
    }

    private Booking findBooking(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    private Guest findGuest(String id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Guest not found"));
    }

    private Room findRoom(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    private void requireAvailable(Room room) {
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BadRequestException("Room is not available");
        }
    }

    private void requireValidDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out must be after check-in");
        }
    }

    private void requireStatus(Booking booking, BookingStatus expected, String message) {
        if (booking.getStatus() != expected) {
            throw new BadRequestException(message);
        }
    }
}
