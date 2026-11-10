package com.tecnocampus.tecnohostal.application;

import com.tecnocampus.tecnohostal.application.dto.CreateGuestDTO;
import com.tecnocampus.tecnohostal.application.dto.GuestDTO;
import com.tecnocampus.tecnohostal.domain.Guest;
import com.tecnocampus.tecnohostal.domain.VipLevel;
import com.tecnocampus.tecnohostal.exception.BadRequestException;
import com.tecnocampus.tecnohostal.exception.ConflictException;
import com.tecnocampus.tecnohostal.exception.NotFoundException;
import com.tecnocampus.tecnohostal.persistence.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
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
