package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Base comuna de totes les classes de test. NO LA MODIFIQUIS.
 * <p>
 * Abans de CADA test es tornen a executar schema.sql i data.sql: cada test comença amb les dades
 * inicials de data.sql, així que els tests no depenen els uns dels altres ni de l'ordre.
 * <p>
 * Els ids que fan servir els tests són els de data.sql (room-101, guest-anna, bk-08...).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SqlConfig(encoding = "UTF-8")
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class ApiTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    /** GET d'una URL. */
    protected ResultActions getJson(String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    /** POST amb un body JSON. */
    protected ResultActions postJson(String url, String json) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    /** POST sense body. */
    protected ResultActions postEmpty(String url) throws Exception {
        return mockMvc.perform(post(url));
    }

    /** POST /bookings amb les dades d'una reserva. Dates en format ISO: "2026-12-01". */
    protected ResultActions book(String guestId, String roomId, String checkIn, String checkOut) throws Exception {
        return postJson("/bookings", """
                {"guestId": "%s", "roomId": "%s", "checkIn": "%s", "checkOut": "%s"}
                """.formatted(guestId, roomId, checkIn, checkOut));
    }

    /** POST /bookings/{id}/check-out amb el body {"roomCondition": N} (Ticket #3). */
    protected ResultActions checkOutWithCondition(String bookingId, int roomCondition) throws Exception {
        return postJson("/bookings/" + bookingId + "/check-out", """
                {"roomCondition": %d}
                """.formatted(roomCondition));
    }
}
