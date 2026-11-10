package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TICKET #3 · Check-out amb l'estat de l'habitació. NO MODIFIQUIS AQUEST FITXER.
 * <p>
 * POST /bookings/{id}/check-out amb el body {"roomCondition": N}:
 * N &gt;= 50 l'habitació torna a AVAILABLE; N &lt; 50 passa a MAINTENANCE;
 * N &lt; 30 també es cobra una nit més (preu per nit de l'habitació) com a penalització.
 * En tots els casos es guarda N com a condition de l'habitació.
 * <p>
 * bk-08: Marc a la 103 (SINGLE, 70 €/nit), total 210 €. bk-09: Laia a la 203 (DOUBLE, 110 €/nit), total 330 €.
 */
@DisplayName("Ticket #3 · Check-out amb estat de l'habitació")
class Ticket3_CheckOutConditionTest extends ApiTestSupport {

    @Test
    @DisplayName("Amb condició 50 l'habitació torna a AVAILABLE i es guarda la condició")
    void test1_goodConditionRoomAvailable() throws Exception {
        checkOutWithCondition("bk-08", 50)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_OUT"))
                .andExpect(jsonPath("$.totalPrice", closeTo(210.0, 0.01)));

        getJson("/rooms/room-103")
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.condition").value(50));
    }

    @Test
    @DisplayName("Amb condició 40 l'habitació passa a MAINTENANCE però no es cobra res més")
    void test2_badConditionRoomToMaintenance() throws Exception {
        checkOutWithCondition("bk-09", 40)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_OUT"))
                .andExpect(jsonPath("$.totalPrice", closeTo(330.0, 0.01)));

        getJson("/rooms/room-203")
                .andExpect(jsonPath("$.status").value("MAINTENANCE"))
                .andExpect(jsonPath("$.condition").value(40));
    }

    @Test
    @DisplayName("Amb condició 20 l'habitació passa a MAINTENANCE i es cobra una nit més")
    void test3_damagedRoomChargesOneExtraNight() throws Exception {
        checkOutWithCondition("bk-08", 20)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_OUT"))
                .andExpect(jsonPath("$.totalPrice", closeTo(280.0, 0.01)));   // 210 + 70

        getJson("/rooms/room-103")
                .andExpect(jsonPath("$.status").value("MAINTENANCE"))
                .andExpect(jsonPath("$.condition").value(20));

        // El preu nou també s'ha de guardar a la base de dades
        getJson("/bookings/bk-08")
                .andExpect(jsonPath("$.totalPrice", closeTo(280.0, 0.01)));
    }
}
