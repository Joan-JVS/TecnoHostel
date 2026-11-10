package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TICKET #6 · Informe d'ingressos per tipus d'habitació. NO MODIFIQUIS AQUEST FITXER.
 * <p>
 * GET /reports/revenue retorna una llista de RoomTypeRevenueDTO, una fila per tipus
 * (SINGLE, DOUBLE, SUITE), ordenada per totalRevenue de més gran a més petit.
 * Només compten les reserves CHECKED_OUT.
 * <p>
 * Amb data.sql: SUITE 540 + 440 = 980 (2 estades, 3 habitacions);
 * DOUBLE 270 + 180 + 330 = 780 (3 estades, 4 habitacions);
 * SINGLE 240 + 120 = 360 (2 estades, 3 habitacions).
 */
@DisplayName("Ticket #6 · Informe d'ingressos")
class Ticket6_RevenueReportTest extends ApiTestSupport {

    @Test
    @DisplayName("L'informe té una fila per tipus, ordenades per ingressos de més a menys")
    void test1_oneRowPerTypeSortedByRevenue() throws Exception {
        getJson("/reports/revenue")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].roomType", contains("SUITE", "DOUBLE", "SINGLE")));

        // Laia fa el check-out de la 203 (DOUBLE, 330 €): DOUBLE passa a 1110 € i avança SUITE
        postEmpty("/bookings/bk-09/check-out").andExpect(status().isOk());

        getJson("/reports/revenue")
                .andExpect(jsonPath("$[*].roomType", contains("DOUBLE", "SUITE", "SINGLE")));
    }

    @Test
    @DisplayName("totalRevenue, checkedOutBookings i totalRooms són correctes per a cada tipus")
    void test2_totalsPerType() throws Exception {
        getJson("/reports/revenue")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalRevenue", closeTo(980.0, 0.01)))
                .andExpect(jsonPath("$[0].checkedOutBookings").value(2))
                .andExpect(jsonPath("$[0].totalRooms").value(3))
                .andExpect(jsonPath("$[1].totalRevenue", closeTo(780.0, 0.01)))
                .andExpect(jsonPath("$[1].checkedOutBookings").value(3))
                .andExpect(jsonPath("$[1].totalRooms").value(4))
                .andExpect(jsonPath("$[2].totalRevenue", closeTo(360.0, 0.01)))
                .andExpect(jsonPath("$[2].checkedOutBookings").value(2))
                .andExpect(jsonPath("$[2].totalRooms").value(3));
    }

    @Test
    @DisplayName("averageBookingPrice és correcte i l'informe es recalcula després d'un check-out")
    void test3_averageAndLiveRecalculation() throws Exception {
        getJson("/reports/revenue")
                .andExpect(jsonPath("$[0].averageBookingPrice", closeTo(490.0, 0.01)))
                .andExpect(jsonPath("$[1].averageBookingPrice", closeTo(260.0, 0.01)))
                .andExpect(jsonPath("$[2].averageBookingPrice", closeTo(180.0, 0.01)));

        // Marc fa el check-out de la 103 (SINGLE, 210 €): SINGLE passa a 570 € en 3 estades
        postEmpty("/bookings/bk-08/check-out").andExpect(status().isOk());

        getJson("/reports/revenue")
                .andExpect(jsonPath("$[2].roomType").value("SINGLE"))
                .andExpect(jsonPath("$[2].totalRevenue", closeTo(570.0, 0.01)))
                .andExpect(jsonPath("$[2].checkedOutBookings").value(3))
                .andExpect(jsonPath("$[2].averageBookingPrice", closeTo(190.0, 0.01)));
    }
}
