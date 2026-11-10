package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TICKET #5 · Historial d'un client. NO MODIFIQUIS AQUEST FITXER.
 * <p>
 * GET /guests/{id}/history retorna un GuestHistoryDTO. Només les reserves CHECKED_OUT compten
 * per a totalSpent i totalStays; la llista bookings les porta totes, de la més nova a la més antiga.
 * <p>
 * Núria: bk-02 (540, CHECKED_OUT), bk-04 (440, CHECKED_OUT), bk-10 (360, CONFIRMED).
 * Anna: bk-01 (270, CHECKED_OUT, 1/9), bk-06 (120, CHECKED_OUT, 10/10), bk-11 (360, CONFIRMED, 10/12).
 * Oriol no té cap reserva.
 */
@DisplayName("Ticket #5 · Historial del client")
class Ticket5_GuestHistoryTest extends ApiTestSupport {

    @Test
    @DisplayName("totalSpent i totalStays només compten les estades CHECKED_OUT")
    void test1_totalsOnlyCountCheckedOutStays() throws Exception {
        getJson("/guests/guest-nuria/history")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guest.id").value("guest-nuria"))
                .andExpect(jsonPath("$.guest.vipLevel").value("GOLD"))
                .andExpect(jsonPath("$.totalSpent", closeTo(980.0, 0.01)))
                .andExpect(jsonPath("$.totalStays").value(2));
    }

    @Test
    @DisplayName("bookings porta totes les reserves, de la més nova a la més antiga")
    void test2_bookingsSortedNewestFirst() throws Exception {
        getJson("/guests/guest-anna/history")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings", hasSize(3)))
                .andExpect(jsonPath("$.bookings[*].id", contains("bk-11", "bk-06", "bk-01")))
                .andExpect(jsonPath("$.totalSpent", closeTo(390.0, 0.01)))
                .andExpect(jsonPath("$.totalStays").value(2));
    }

    @Test
    @DisplayName("Un client sense reserves té l'historial buit; un client que no existeix dona 404")
    void test3_emptyHistoryAndUnknownGuest() throws Exception {
        getJson("/guests/guest-oriol/history")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent", closeTo(0.0, 0.01)))
                .andExpect(jsonPath("$.totalStays").value(0))
                .andExpect(jsonPath("$.bookings", hasSize(0)));

        getJson("/guests/guest-999/history")
                .andExpect(status().isNotFound());
    }
}
