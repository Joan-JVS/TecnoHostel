package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TICKET #2 · Cerca d'habitacions amb filtres. NO MODIFIQUIS AQUEST FITXER.
 * <p>
 * GET /rooms/search amb els paràmetres opcionals type, maxPrice i status.
 * Si no s'envia status, només surten les habitacions AVAILABLE.
 * <p>
 * A data.sql hi ha 6 habitacions AVAILABLE (101, 102, 201, 202, 301, 302),
 * 2 OCCUPIED (103, 203) i 2 MAINTENANCE (204, 303).
 */
@DisplayName("Ticket #2 · Cerca d'habitacions")
class Ticket2_RoomSearchTest extends ApiTestSupport {

    @Test
    @DisplayName("Filtrar per tipus retorna només les habitacions d'aquest tipus que estan AVAILABLE")
    void test1_filterByType() throws Exception {
        getJson("/rooms/search?type=DOUBLE")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].roomNumber", containsInAnyOrder("201", "202")))
                .andExpect(jsonPath("$[*].type", everyItem(is("DOUBLE"))))
                .andExpect(jsonPath("$[*].status", everyItem(is("AVAILABLE"))));
    }

    @Test
    @DisplayName("maxPrice deixa fora les habitacions més cares i es pot combinar amb type")
    void test2_filterByMaxPriceAndCombine() throws Exception {
        getJson("/rooms/search?maxPrice=90&status=AVAILABLE")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].roomNumber", containsInAnyOrder("101", "102", "201", "202")))
                .andExpect(jsonPath("$[*].pricePerNight", everyItem(lessThanOrEqualTo(90.0))));

        getJson("/rooms/search?type=SINGLE&maxPrice=60&status=AVAILABLE")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].roomNumber", containsInAnyOrder("101", "102")));
    }

    @Test
    @DisplayName("Sense paràmetres surten les AVAILABLE; amb status es poden buscar les altres")
    void test3_defaultStatusIsAvailable() throws Exception {
        getJson("/rooms/search")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[*].status", everyItem(is("AVAILABLE"))));

        getJson("/rooms/search?status=MAINTENANCE")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].roomNumber", containsInAnyOrder("204", "303")));

        getJson("/rooms/search?type=SUITE&status=MAINTENANCE")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].roomNumber", contains("303")));
    }
}
