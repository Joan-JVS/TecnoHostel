package com.tecnocampus.tecnohostal.persistence;

import com.tecnocampus.tecnohostal.domain.Guest;
import com.tecnocampus.tecnohostal.domain.VipLevel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Taula guests, amb JdbcClient. */
@Repository
public class GuestRepository {

    private final JdbcClient jdbc;

    /** Converteix una fila de la taula guests en un Guest. */
    private static final RowMapper<Guest> GUEST_MAPPER = (rs, rowNum) -> new Guest(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            VipLevel.valueOf(rs.getString("vip_level")));

    public GuestRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Guest> findAll() {
        return jdbc.sql("SELECT * FROM guests ORDER BY name")
                .query(GUEST_MAPPER)
                .list();
    }

    public Optional<Guest> findById(String id) {
        return jdbc.sql("SELECT * FROM guests WHERE id = :id")
                .param("id", id)
                .query(GUEST_MAPPER)
                .optional();
    }

    public Optional<Guest> findByEmail(String email) {
        return jdbc.sql("SELECT * FROM guests WHERE email = :email")
                .param("email", email)
                .query(GUEST_MAPPER)
                .optional();
    }

    /** INSERT d'un client nou. */
    public void save(Guest guest) {
        jdbc.sql("""
                INSERT INTO guests (id, name, email, phone, vip_level)
                VALUES (:id, :name, :email, :phone, :vipLevel)""")
                .param("id", guest.getId())
                .param("name", guest.getName())
                .param("email", guest.getEmail())
                .param("phone", guest.getPhone())
                .param("vipLevel", guest.getVipLevel().name())
                .update();
    }
}
