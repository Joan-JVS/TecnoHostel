package com.tecnocampus.tecnohostal.api;

import com.tecnocampus.tecnohostal.application.BookingService;
import com.tecnocampus.tecnohostal.application.dto.BookingDTO;
import com.tecnocampus.tecnohostal.application.dto.CheckOutDTO;
import com.tecnocampus.tecnohostal.application.dto.CreateBookingDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingRestController {

    private final BookingService bookingService;

    public BookingRestController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public BookingDTO getBookingById(@PathVariable String id) {
        return bookingService.getBookingById(id);
    }

    /** Retorna 201 Created i la capçalera Location amb la URL de la reserva nova. */
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody CreateBookingDTO dto) {
        BookingDTO created = bookingService.createBooking(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/{id}/check-in")
    public BookingDTO checkIn(@PathVariable String id) {
        return bookingService.checkIn(id);
    }

    /**
     * El body és OPCIONAL (required = false). Sense body, checkOutDTO arriba com a null.
     * Ticket #3: no treguis el required = false o els check-outs sense body tornaran 400.
     */
    @PostMapping("/{id}/check-out")
    public BookingDTO checkOut(@PathVariable String id,
                               @RequestBody(required = false) CheckOutDTO checkOutDTO) {
        Integer roomCondition = (checkOutDTO != null) ? checkOutDTO.roomCondition() : null;
        return bookingService.checkOut(id, roomCondition);
    }

    @PostMapping("/{id}/cancel")
    public BookingDTO cancelBooking(@PathVariable String id) {
        return bookingService.cancelBooking(id);
    }
}
