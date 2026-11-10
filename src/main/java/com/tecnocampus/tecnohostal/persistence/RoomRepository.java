package com.tecnocampus.tecnohostal.persistence;

import com.tecnocampus.tecnohostal.domain.Room;
import com.tecnocampus.tecnohostal.domain.RoomStatus;
import com.tecnocampus.tecnohostal.domain.RoomType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Taula rooms, amb JdbcClient. */
@Repository
public class RoomRepository {

    private final JdbcClient jdbc;

    /** Converteix una fila de la taula rooms en un Room. */
    private static final RowMapper<Room> ROOM_MAPPER = (rs, rowNum) -> new Room(
            rs.getString("id"),
            rs.getString("room_number"),
            RoomType.valueOf(rs.getString("type")),
            rs.getDouble("price_per_night"),
            RoomStatus.valueOf(rs.getString("status")),
            rs.getInt("room_condition"));

    public RoomRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Room> findAll() {
        return jdbc.sql("SELECT * FROM rooms ORDER BY room_number")
                .query(ROOM_MAPPER)
                .list();
    }

    public Optional<Room> findById(String id) {
        return jdbc.sql("SELECT * FROM rooms WHERE id = :id")
                .param("id", id)
                .query(ROOM_MAPPER)
                .optional();
    }

    public Optional<Room> findByRoomNumber(String roomNumber) {
        return jdbc.sql("SELECT * FROM rooms WHERE room_number = :roomNumber")
                .param("roomNumber", roomNumber)
                .query(ROOM_MAPPER)
                .optional();
    }

    /** INSERT d'una habitació nova. */
    public void save(Room room) {
        jdbc.sql("""
                INSERT INTO rooms (id, room_number, type, price_per_night, status, room_condition)
                VALUES (:id, :roomNumber, :type, :price, :status, :condition)""")
                .param("id", room.getId())
                .param("roomNumber", room.getRoomNumber())
                .param("type", room.getType().name())
                .param("price", room.getPricePerNight())
                .param("status", room.getStatus().name())
                .param("condition", room.getCondition())
                .update();
    }

    /** UPDATE de tots els camps que poden canviar. Retorna 1 si l'habitació existia, 0 si no. */
    public int update(Room room) {
        return jdbc.sql("""
                UPDATE rooms SET room_number = :roomNumber, type = :type, price_per_night = :price,
                       status = :status, room_condition = :condition
                WHERE id = :id""")
                .param("roomNumber", room.getRoomNumber())
                .param("type", room.getType().name())
                .param("price", room.getPricePerNight())
                .param("status", room.getStatus().name())
                .param("condition", room.getCondition())
                .param("id", room.getId())
                .update();
    }
}
