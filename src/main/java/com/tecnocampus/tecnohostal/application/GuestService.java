package com.tecnocampus.tecnohostal.application;

import com.tecnocampus.tecnohostal.application.dto.CreateGuestDTO;
import com.tecnocampus.tecnohostal.application.dto.GuestDTO;
import com.tecnocampus.tecnohostal.domain.Guest;
import com.tecnocampus.tecnohostal.domain.VipLevel;
import com.tecnocampus.tecnohostal.exception.BadRequestException;
import com.tecnocampus.tecnohostal.exception.ConflictException;
import com.tecnocampus.tecnohostal.exception.NotFoundException;
import com.tecnocampus.tecnohostal.persistence.GuestRepository;
import com.tecnocampus.tecnohostal.persistence.BookingRepository;
import com.tecnocampus.tecnohostal.application.dto.GuestHistoryDTO;
import com.tecnocampus.tecnohostal.application.dto.BookingDTO;
import com.tecnocampus.tecnohostal.domain.Booking;
import com.tecnocampus.tecnohostal.domain.BookingStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;

    public GuestService(GuestRepository guestRepository, BookingRepository bookingRepository) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<GuestDTO> getAllGuests() {
        return guestRepository.findAll().stream().map(GuestDTO::from).toList();
    }

    public GuestDTO getGuestById(String id) {
        return GuestDTO.from(findGuest(id));
    }

    public GuestDTO createGuest(CreateGuestDTO dto) {
        guestRepository.findByEmail(dto.email()).ifPresent(g -> {
            throw new ConflictException("Email already exists");
        });
        Guest guest = new Guest(dto.name(), dto.email(), dto.phone(), parseVipLevel(dto.vipLevel()));
        guestRepository.save(guest);
        return GuestDTO.from(guest);
    }

    // TODO Ticket #5: public GuestHistoryDTO getGuestHistory(String guestId)
    //  - Si el client no existeix: 404 (ja tens findGuest a sota).
    //  - Necessites les reserves del client: les dona el BookingRepository, que encara no està
    //    injectat en aquest servei.
    //  - totalSpent i totalStays només compten les reserves CHECKED_OUT.
    //  - bookings: TOTES les reserves del client com a BookingDTO, de la més nova a la més antiga
    //    (per checkIn). BookingDTO.from(booking) ja fa la conversió.
    public GuestHistoryDTO getGuestHistory(String guestId){
        Guest guest = findGuest(guestId);

        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> b.getGuest().getId().equals(guestId))
                .sorted((b1, b2) -> b2.getCheckIn().compareTo(b1.getCheckIn()))
                .toList();

        double totalSpent = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CHECKED_OUT)
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        int totalStays = (int) bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CHECKED_OUT)
                .count();

        List<BookingDTO> bookingDTOS = bookings.stream()
                .map(BookingDTO::from)
                .toList();

        return new GuestHistoryDTO(
                GuestDTO.from(guest),
                totalSpent,
                totalStays,
                bookingDTOS);
    }

    private Guest findGuest(String id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Guest not found"));
    }

    private VipLevel parseVipLevel(String vipLevel) {
        if (vipLevel == null || vipLevel.isBlank()) {
            return VipLevel.STANDARD;
        }
        try {
            return VipLevel.valueOf(vipLevel);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("VIP level must be STANDARD, SILVER or GOLD");
        }
    }
}
