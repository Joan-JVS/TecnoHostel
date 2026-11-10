package com.tecnocampus.tecnohostal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Funcionalitats que JA funcionen. NO MODIFIQUIS AQUEST FITXER.
 * <p>
 * Aquests tests no sumen punts: són la prova que no has trencat res. Han de continuar en verd
 * quan acabis els tickets.
 */
@DisplayName("Funcionalitats existents (no les trenquis)")
class ExistingFeatureTest extends ApiTestSupport {

    // ---------- Habitacions ----------

    @Test
    @DisplayName("GET /rooms retorna les 10 habitacions")
    void test01_getAllRooms() throws Exception {
        getJson("/rooms")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    @DisplayName("GET /rooms/{id} retorna una habitació")
    void test02_getRoomById() throws Exception {
        getJson("/rooms/room-301")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("301"))
                .andExpect(jsonPath("$.type").value("SUITE"))
                .andExpect(jsonPath("$.pricePerNight", closeTo(180.0, 0.01)))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.condition").value(100));
    }

    @Test
    @DisplayName("GET /rooms/{id} d'una habitació que no existeix dona 404")
    void test03_getRoomNotFound() throws Exception {
        getJson("/rooms/room-999")
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /rooms crea una habitació AVAILABLE i en estat 100")
    void test04_createRoom() throws Exception {
        postJson("/rooms", """
                {"roomNumber": "401", "type": "DOUBLE", "pricePerNight": 95.0}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyOrNullString())))
                .andExpect(jsonPath("$.roomNumber").value("401"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.condition").value(100));

        getJson("/rooms").andExpect(jsonPath("$", hasSize(11)));
    }

    @Test
    @DisplayName("POST /rooms amb un número que ja existeix dona 409")
    void test05_createRoomDuplicateNumber() throws Exception {
        postJson("/rooms", """
                {"roomNumber": "101", "type": "SINGLE", "pricePerNight": 60.0}
                """)
                .andExpect(status().isConflict());
    }

    // ---------- Clients ----------

    @Test
    @DisplayName("GET /guests retorna els 6 clients")
    void test06_getAllGuests() throws Exception {
        getJson("/guests")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("POST /guests sense vipLevel crea un client STANDARD")
    void test07_createGuest() throws Exception {
        postJson("/guests", """
                {"name": "Joan Serra", "email": "joan.serra@tecnohostal.cat", "phone": "+34 600 777 777"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyOrNullString())))
                .andExpect(jsonPath("$.vipLevel").value("STANDARD"));
    }

    @Test
    @DisplayName("POST /guests amb un email repetit dona 409")
    void test08_createGuestDuplicateEmail() throws Exception {
        postJson("/guests", """
                {"name": "Una altra Anna", "email": "anna.puig@tecnohostal.cat"}
                """)
                .andExpect(status().isConflict());
    }

    // ---------- Reserves ----------

    @Test
    @DisplayName("GET /bookings retorna les 12 reserves")
    void test09_getAllBookings() throws Exception {
        getJson("/bookings")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(12)));
    }

    @Test
    @DisplayName("GET /bookings/{id} retorna la reserva amb el nom del client i el número d'habitació")
    void test10_getBookingById() throws Exception {
        getJson("/bookings/bk-01")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guestName").value("Anna Puig"))
                .andExpect(jsonPath("$.roomNumber").value("201"))
                .andExpect(jsonPath("$.status").value("CHECKED_OUT"))
                .andExpect(jsonPath("$.totalPrice", closeTo(270.0, 0.01)));
    }

    @Test
    @DisplayName("POST /bookings d'un client STANDARD: 201, CONFIRMED i preu = nits x preu per nit")
    void test11_createBooking() throws Exception {
        book("guest-marc", "room-302", "2027-03-01", "2027-03-03")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/bookings/")))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalPrice", closeTo(440.0, 0.01)));
    }

    @Test
    @DisplayName("POST /bookings d'una habitació OCCUPIED dona 400")
    void test12_createBookingRoomNotAvailable() throws Exception {
        book("guest-anna", "room-103", "2027-03-01", "2027-03-03")
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /bookings amb la sortida abans de l'entrada dona 400")
    void test13_createBookingInvalidDates() throws Exception {
        book("guest-anna", "room-101", "2027-03-05", "2027-03-01")
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Check-in: la reserva passa a CHECKED_IN i l'habitació a OCCUPIED")
    void test14_checkIn() throws Exception {
        postEmpty("/bookings/bk-10/check-in")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_IN"));

        getJson("/rooms/room-201").andExpect(jsonPath("$.status").value("OCCUPIED"));
    }

    @Test
    @DisplayName("Check-out SENSE body: la reserva passa a CHECKED_OUT i l'habitació torna a AVAILABLE")
    void test15_checkOutWithoutBody() throws Exception {
        postEmpty("/bookings/bk-08/check-out")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_OUT"))
                .andExpect(jsonPath("$.totalPrice", closeTo(210.0, 0.01)));

        getJson("/rooms/room-103").andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("Cancel·lar una reserva CONFIRMED funciona; cancel·lar-la dues vegades dona 400")
    void test16_cancelBooking() throws Exception {
        postEmpty("/bookings/bk-11/cancel")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        postEmpty("/bookings/bk-11/cancel")
                .andExpect(status().isBadRequest());
    }
}
