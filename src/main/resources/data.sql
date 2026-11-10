-- TecnoHostal · dades inicials
-- Els ids són llegibles a propòsit (room-101, guest-anna, bk-08) perquè siguin fàcils de fer servir
-- a request.http i als tests. Els registres nous que creïs tindran un UUID.

-- Habitacions (10)
INSERT INTO rooms (id, room_number, type, price_per_night, status, room_condition) VALUES
('room-101', '101', 'SINGLE',  60.0, 'AVAILABLE',   100),
('room-102', '102', 'SINGLE',  60.0, 'AVAILABLE',   100),
('room-103', '103', 'SINGLE',  70.0, 'OCCUPIED',     90),
('room-201', '201', 'DOUBLE',  90.0, 'AVAILABLE',   100),
('room-202', '202', 'DOUBLE',  90.0, 'AVAILABLE',    95),
('room-203', '203', 'DOUBLE', 110.0, 'OCCUPIED',    100),
('room-204', '204', 'DOUBLE', 110.0, 'MAINTENANCE',  40),
('room-301', '301', 'SUITE',  180.0, 'AVAILABLE',   100),
('room-302', '302', 'SUITE',  220.0, 'AVAILABLE',   100),
('room-303', '303', 'SUITE',  220.0, 'MAINTENANCE',  25);

-- Clients (6)
INSERT INTO guests (id, name, email, phone, vip_level) VALUES
('guest-anna',  'Anna Puig',    'anna.puig@tecnohostal.cat',    '+34 600 111 111', 'STANDARD'),
('guest-marc',  'Marc Soler',   'marc.soler@tecnohostal.cat',   '+34 600 222 222', 'STANDARD'),
('guest-laia',  'Laia Vidal',   'laia.vidal@tecnohostal.cat',   '+34 600 333 333', 'SILVER'),
('guest-pau',   'Pau Roca',     'pau.roca@tecnohostal.cat',     '+34 600 444 444', 'SILVER'),
('guest-nuria', 'Núria Ferrer', 'nuria.ferrer@tecnohostal.cat', '+34 600 555 555', 'GOLD'),
('guest-oriol', 'Oriol Mas',    'oriol.mas@tecnohostal.cat',    '+34 600 666 666', 'STANDARD');

-- Estades acabades (CHECKED_OUT)
INSERT INTO bookings (id, guest_id, room_id, check_in, check_out, status, total_price) VALUES
('bk-01', 'guest-anna',  'room-201', '2026-09-01', '2026-09-04', 'CHECKED_OUT', 270.0),
('bk-02', 'guest-nuria', 'room-301', '2026-09-05', '2026-09-08', 'CHECKED_OUT', 540.0),
('bk-03', 'guest-laia',  'room-101', '2026-09-10', '2026-09-14', 'CHECKED_OUT', 240.0),
('bk-04', 'guest-nuria', 'room-302', '2026-10-01', '2026-10-03', 'CHECKED_OUT', 440.0),
('bk-05', 'guest-pau',   'room-202', '2026-10-05', '2026-10-07', 'CHECKED_OUT', 180.0),
('bk-06', 'guest-anna',  'room-102', '2026-10-10', '2026-10-12', 'CHECKED_OUT', 120.0),
('bk-07', 'guest-marc',  'room-203', '2026-10-15', '2026-10-18', 'CHECKED_OUT', 330.0);

-- Clients que ara mateix són a l'hostal (CHECKED_IN): les habitacions 103 i 203 estan OCCUPIED
INSERT INTO bookings (id, guest_id, room_id, check_in, check_out, status, total_price) VALUES
('bk-08', 'guest-marc', 'room-103', '2026-11-15', '2026-11-18', 'CHECKED_IN', 210.0),
('bk-09', 'guest-laia', 'room-203', '2026-11-16', '2026-11-19', 'CHECKED_IN', 330.0);

-- Reserves futures (CONFIRMED)
INSERT INTO bookings (id, guest_id, room_id, check_in, check_out, status, total_price) VALUES
('bk-10', 'guest-nuria', 'room-201', '2026-12-01', '2026-12-05', 'CONFIRMED', 360.0),
('bk-11', 'guest-anna',  'room-301', '2026-12-10', '2026-12-12', 'CONFIRMED', 360.0);

-- Reserva cancel·lada
INSERT INTO bookings (id, guest_id, room_id, check_in, check_out, status, total_price) VALUES
('bk-12', 'guest-pau', 'room-202', '2026-12-01', '2026-12-04', 'CANCELLED', 270.0);
