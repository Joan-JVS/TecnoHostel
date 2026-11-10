package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TICKET #1 · El descompte VIP no s'aplica. NO MODIFIQUIS AQUEST FITXER.
 * <p>
 * Habitació 102 (SINGLE, 60 €/nit), 3 nits = 180 € sense descompte.
 * STANDARD paga 180, SILVER (-10%) paga 162 i GOLD (-15%) paga 153.
 */
@DisplayName("Ticket #1 · Descompte VIP")
class Ticket1_VipDiscountTest extends ApiTestSupport {

    @Test
    @DisplayName("Un client SILVER paga un 10% menys")
    void test1_silverGets10PercentOff() throws Exception {
        book("guest-laia", "room-102", "2027-01-10", "2027-01-13")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice", closeTo(162.0, 0.01)));
    }

    @Test
    @DisplayName("Un client GOLD paga un 15% menys")
    void test2_goldGets15PercentOff() throws Exception {
        book("guest-nuria", "room-102", "2027-01-10", "2027-01-13")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice", closeTo(153.0, 0.01)));
    }

    @Test
    @DisplayName("Un client STANDARD continua pagant el preu sencer i el GOLD té descompte en una SUITE")
    void test3_standardPaysFullPriceAndGoldDiscountOnSuite() throws Exception {
        book("guest-marc", "room-102", "2027-01-10", "2027-01-13")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice", closeTo(180.0, 0.01)));

        // room-302: SUITE a 220 €/nit, 2 nits = 440 €, amb el 15% GOLD = 374 €
        book("guest-nuria", "room-302", "2027-02-01", "2027-02-03")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice", closeTo(374.0, 0.01)));
    }
}
