package com.tecnocampus.tecnohostal.persistence;

import com.tecnocampus.tecnohostal.domain.Booking;
import com.tecnocampus.tecnohostal.domain.BookingStatus;
import com.tecnocampus.tecnohostal.domain.Guest;
import com.tecnocampus.tecnohostal.domain.Room;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Taula bookings, amb JdbcClient.
 * <p>
 * Una fila de bookings només té guest_id i room_id, però un Booking guarda els objectes Guest i
 * Room. Per això la lectura es fa en dos passos:
 * <ol>
 *   <li>{@code mapRow}: llegeix la fila tal com és (un record Row amb els ids).</li>
 *   <li>{@code hydrate}: busca el Guest i el Room d'aquests ids i construeix el Booking.</li>
 * </ol>
 * Per fer una consulta nova, copia el patró de findAll: {@code hydrateAll(... .query(this::mapRow).list())}.
 */
@Repository
public class BookingRepository {

    private final JdbcClient jdbc;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    public BookingRepository(JdbcClient jdbc, GuestRepository guestRepository, RoomRepository roomRepository) {
        this.jdbc = jdbc;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
    }

    /** Una fila de la taula bookings tal com està guardada (amb els ids, no els objectes). */
    private record Row(String id, String guestId, String roomId, LocalDate checkIn,
                       LocalDate checkOut, BookingStatus status, double totalPrice) {
    }

    private Row mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Row(
                rs.getString("id"),
                rs.getString("guest_id"),
                rs.getString("room_id"),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                BookingStatus.valueOf(rs.getString("status")),
                rs.getDouble("total_price"));
    }

    private Booking hydrate(Row row) {
        Guest guest = guestRepository.findById(row.guestId())
                .orElseThrow(() -> new IllegalStateException("Guest " + row.guestId() + " not found"));
        Room room = roomRepository.findById(row.roomId())
                .orElseThrow(() -> new IllegalStateException("Room " + row.roomId() + " not found"));
        return new Booking(row.id(), guest, room, row.checkIn(), row.checkOut(), row.status(), row.totalPrice());
    }

    private List<Booking> hydrateAll(List<Row> rows) {
        return rows.stream().map(this::hydrate).toList();
    }

    public List<Booking> findAll() {
        return hydrateAll(jdbc.sql("SELECT * FROM bookings ORDER BY check_in")
                .query(this::mapRow)
                .list());
    }

    public Optional<Booking> findById(String id) {
        return jdbc.sql("SELECT * FROM bookings WHERE id = :id")
                .param("id", id)
                .query(this::mapRow)
                .optional()
                .map(this::hydrate);
    }

    /** INSERT d'una reserva nova. */
    public void save(Booking booking) {
        jdbc.sql("""
                INSERT INTO bookings (id, guest_id, room_id, check_in, check_out, status, total_price)
                VALUES (:id, :guestId, :roomId, :checkIn, :checkOut, :status, :totalPrice)""")
                .param("id", booking.getId())
                .param("guestId", booking.getGuest().getId())
                .param("roomId", booking.getRoom().getId())
                .param("checkIn", booking.getCheckIn())
                .param("checkOut", booking.getCheckOut())
                .param("status", booking.getStatus().name())
                .param("totalPrice", booking.getTotalPrice())
                .update();
    }

    /** UPDATE dels camps que poden canviar (estat i preu). Retorna 1 si existia, 0 si no. */
    public int update(Booking booking) {
        return jdbc.sql("UPDATE bookings SET status = :status, total_price = :totalPrice WHERE id = :id")
                .param("status", booking.getStatus().name())
                .param("totalPrice", booking.getTotalPrice())
                .param("id", booking.getId())
                .update();
    }
}
