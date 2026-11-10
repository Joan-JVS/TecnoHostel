package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TICKET #4 · Es poden fer dues reserves de la mateixa habitació per a les mateixes dates.
 * NO MODIFIQUIS AQUEST FITXER.
 * <p>
 * bk-10: Núria té la 201 reservada (CONFIRMED) de l'1 al 5 de desembre de 2026.
 * bk-12: Pau tenia la 202 de l'1 al 4 de desembre, però la va cancel·lar (CANCELLED).
 */
@DisplayName("Ticket #4 · Reserves solapades")
class Ticket4_DoubleBookingTest extends ApiTestSupport {

    @Test
    @DisplayName("Reservar unes dates que se solapen amb una reserva CONFIRMED dona 400")
    void test1_overlappingBookingIsRejected() throws Exception {
        book("guest-marc", "room-201", "2026-12-03", "2026-12-07")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Room is already booked for the requested dates"));
    }

    @Test
    @DisplayName("Entrar el mateix dia que surt l'altre client està permès; solapar-se per davant no")
    void test2_adjacentDatesAreAllowed() throws Exception {
        // Núria surt el 5/12: entrar el 5/12 és correcte
        book("guest-anna", "room-201", "2026-12-05", "2026-12-08")
                .andExpect(status().isCreated());

        // Del 28/11 al 2/12 se solapa amb Núria (entra l'1/12)
        book("guest-pau", "room-201", "2026-11-28", "2026-12-02")
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Una reserva CANCELLED no bloqueja l'habitació, però la nova reserva sí")
    void test3_cancelledBookingsDoNotBlock() throws Exception {
        // La reserva de Pau a la 202 està cancel·lada: aquestes dates estan lliures
        book("guest-marc", "room-202", "2026-12-02", "2026-12-03")
                .andExpect(status().isCreated());

        // Ara la reserva de Marc (CONFIRMED) sí que bloqueja aquestes dates
        book("guest-oriol", "room-202", "2026-12-01", "2026-12-04")
                .andExpect(status().isBadRequest());
    }
}
