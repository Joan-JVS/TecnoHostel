package com.tecnocampus.tecnohostal.application;

import com.tecnocampus.tecnohostal.application.dto.CreateRoomDTO;
import com.tecnocampus.tecnohostal.application.dto.RoomDTO;
import com.tecnocampus.tecnohostal.domain.Room;
import com.tecnocampus.tecnohostal.domain.RoomType;
import com.tecnocampus.tecnohostal.exception.BadRequestException;
import com.tecnocampus.tecnohostal.exception.ConflictException;
import com.tecnocampus.tecnohostal.exception.NotFoundException;
import com.tecnocampus.tecnohostal.persistence.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream().map(RoomDTO::from).toList();
    }

    public RoomDTO getRoomById(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
        return RoomDTO.from(room);
    }

    public RoomDTO createRoom(CreateRoomDTO dto) {
        roomRepository.findByRoomNumber(dto.roomNumber()).ifPresent(r -> {
            throw new ConflictException("Room number already exists");
        });
        Room room = new Room(dto.roomNumber(), parseType(dto.type()), dto.pricePerNight());
        roomRepository.save(room);
        return RoomDTO.from(room);
    }

    // TODO Ticket #2: mètode que rep els filtres (type, maxPrice, status) i retorna una List<RoomDTO>.
    public List<R>

    private RoomType parseType(String type) {
        try {
            return RoomType.valueOf(type);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException("Room type must be SINGLE, DOUBLE or SUITE");
        }
    }
}
