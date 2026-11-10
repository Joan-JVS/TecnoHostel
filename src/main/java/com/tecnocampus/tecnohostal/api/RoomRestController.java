package com.tecnocampus.tecnohostal.api;

import com.tecnocampus.tecnohostal.application.RoomService;
import com.tecnocampus.tecnohostal.application.dto.CreateRoomDTO;
import com.tecnocampus.tecnohostal.application.dto.RoomDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomRestController {

    private final RoomService roomService;

    public RoomRestController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomDTO> getAllRooms() {
        return roomService.getAllRooms();
    }

    // TODO Ticket #2: GET /rooms/search amb tres @RequestParam opcionals: type, maxPrice i status.
    //  Si no arriba status, ha de valer AVAILABLE (mira l'atribut defaultValue de @RequestParam).
    //  No pateixis per /{id}: Spring dona prioritat a una ruta fixa com /search.

    @GetMapping("/{id}")
    public RoomDTO getRoomById(@PathVariable String id) {
        return roomService.getRoomById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDTO createRoom(@RequestBody CreateRoomDTO dto) {
        return roomService.createRoom(dto);
    }
}
