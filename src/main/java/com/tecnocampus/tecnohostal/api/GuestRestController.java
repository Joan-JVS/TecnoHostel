package com.tecnocampus.tecnohostal.api;

import com.tecnocampus.tecnohostal.application.GuestService;
import com.tecnocampus.tecnohostal.application.dto.CreateGuestDTO;
import com.tecnocampus.tecnohostal.application.dto.GuestDTO;
import com.tecnocampus.tecnohostal.application.dto.GuestHistoryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestRestController {

    private final GuestService guestService;

    public GuestRestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public List<GuestDTO> getAllGuests() {
        return guestService.getAllGuests();
    }

    @GetMapping("/{id}")
    public GuestDTO getGuestById(@PathVariable String id) {
        return guestService.getGuestById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuestDTO createGuest(@RequestBody CreateGuestDTO dto) {
        return guestService.createGuest(dto);
    }

    // TODO Ticket #5: GET /guests/{id}/history, retorna un GuestHistoryDTO
    @GetMapping("/{id}/history")
    public GuestHistoryDTO getGuestHistory(@PathVariable String id){
        return guestService.getGuestHistory(id);
    }
}
