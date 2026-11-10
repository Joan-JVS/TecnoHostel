-- TecnoHostal · esquema de la base de dades
-- Comença amb DROP TABLE perquè els tests tornen a executar aquest fitxer abans de cada test.
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS guests;

CREATE TABLE rooms (
    id              VARCHAR(36) PRIMARY KEY,
    room_number     VARCHAR(10) NOT NULL UNIQUE,
    type            VARCHAR(10) NOT NULL,              -- SINGLE, DOUBLE, SUITE
    price_per_night DOUBLE      NOT NULL,
    status          VARCHAR(15) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, OCCUPIED, MAINTENANCE
    room_condition  INT         NOT NULL DEFAULT 100   -- estat físic de l'habitació, de 0 a 100
);

CREATE TABLE guests (
    id        VARCHAR(36)  PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    phone     VARCHAR(20),
    vip_level VARCHAR(10)  NOT NULL DEFAULT 'STANDARD' -- STANDARD, SILVER, GOLD
);

CREATE TABLE bookings (
    id          VARCHAR(36) PRIMARY KEY,
    guest_id    VARCHAR(36) NOT NULL,
    room_id     VARCHAR(36) NOT NULL,
    check_in    DATE        NOT NULL,
    check_out   DATE        NOT NULL,
    status      VARCHAR(15) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    total_price DOUBLE      NOT NULL,
    FOREIGN KEY (guest_id) REFERENCES guests(id),
    FOREIGN KEY (room_id)  REFERENCES rooms(id)
);
