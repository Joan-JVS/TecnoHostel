# TecnoHostal API · Traspàs de projecte

## Benvinguda a l'equip

Acabes d'entrar a l'equip que manté l'API de **TecnoHostal**, un hostal de Mataró amb 10 habitacions. El desenvolupador anterior ha marxat i la gerent ha deixat una llista de 6 peticions: dos errors per arreglar, un canvi en una funcionalitat que ja existeix i tres funcionalitats noves.

El projecte funciona. La teva feina és entendre el codi que hi ha i resoldre els 6 tickets **sense trencar res del que ja funciona**.

## Com funciona la prova

- Durada: **90 minuts**. És individual.
- Cada ticket té la seva classe de test (`Ticket1_VipDiscountTest` ... `Ticket6_RevenueReportTest`) amb **3 tests**. En total, **18 tests**.
- **Nota = tests de tickets que passen / 18 × 10.** Cada test que passa suma, encara que el ticket no estigui acabat del tot.
- `ExistingFeatureTest` (16 tests) no suma punts: comprova que no has trencat res. Ara està tot en verd i així ha d'acabar.
- Els tickets són independents. Abans de cada test es tornen a carregar `schema.sql` i `data.sql`, així que cada test comença sempre amb les mateixes dades. Pots fer els tickets en l'ordre que vulguis.

## Estructura del projecte

```
src/main/java/com/tecnocampus/tecnohostal/
├── api/                ← controladors REST
├── application/        ← serveis (lògica de negoci)
├── application/dto/    ← DTOs (records)
├── domain/             ← entitats (Room, Guest, Booking) i enums
├── exception/          ← excepcions pròpies + GlobalExceptionHandler
└── persistence/        ← repositoris (JdbcClient)

src/main/resources/
├── application.properties
├── schema.sql          ← taules
└── data.sql            ← dades inicials (no el modifiquis: els tests en depenen)

src/test/java/com/tecnocampus/tecnohostal/
├── ApiTestSupport.java         ← base comuna dels tests
├── ExistingFeatureTest.java    ← el que ja funciona
└── Ticket1_... a Ticket6_...   ← un fitxer per ticket
```

## Model de dades

| Entitat | Camps |
|---|---|
| Room | id, roomNumber, type (SINGLE / DOUBLE / SUITE), pricePerNight, status (AVAILABLE / OCCUPIED / MAINTENANCE), condition (0 a 100) |
| Guest | id, name, email (únic), phone, vipLevel (STANDARD / SILVER / GOLD) |
| Booking | id, guest, room, checkIn, checkOut, status (CONFIRMED / CHECKED_IN / CHECKED_OUT / CANCELLED), totalPrice |

Un `Booking` guarda els objectes `Guest` i `Room` sencers. La taula només té `guest_id` i `room_id`: el `BookingRepository` llegeix la fila i carrega el client i l'habitació (mira el comentari de dalt de tot de la classe).

Cicle de vida d'una reserva: `CONFIRMED` → check-in → `CHECKED_IN` → check-out → `CHECKED_OUT`. Una reserva `CONFIRMED` també es pot cancel·lar (`CANCELLED`).

## Dades inicials (`data.sql`)

Els ids són llegibles perquè els puguis fer servir a mà: `room-101`, `guest-anna`, `bk-08`...

**Habitacions**

| id | Tipus | €/nit | Estat | Condició |
|---|---|---|---|---|
| room-101 | SINGLE | 60 | AVAILABLE | 100 |
| room-102 | SINGLE | 60 | AVAILABLE | 100 |
| room-103 | SINGLE | 70 | OCCUPIED | 90 |
| room-201 | DOUBLE | 90 | AVAILABLE | 100 |
| room-202 | DOUBLE | 90 | AVAILABLE | 95 |
| room-203 | DOUBLE | 110 | OCCUPIED | 100 |
| room-204 | DOUBLE | 110 | MAINTENANCE | 40 |
| room-301 | SUITE | 180 | AVAILABLE | 100 |
| room-302 | SUITE | 220 | AVAILABLE | 100 |
| room-303 | SUITE | 220 | MAINTENANCE | 25 |

**Clients:** `guest-anna` (STANDARD), `guest-marc` (STANDARD), `guest-laia` (SILVER), `guest-pau` (SILVER), `guest-nuria` (GOLD) i `guest-oriol` (STANDARD, sense cap reserva).

**Reserves**

| id | Client | Habitació | Dates | Estat | Total |
|---|---|---|---|---|---|
| bk-01 | Anna | 201 | 01/09 a 04/09 | CHECKED_OUT | 270 |
| bk-02 | Núria | 301 | 05/09 a 08/09 | CHECKED_OUT | 540 |
| bk-03 | Laia | 101 | 10/09 a 14/09 | CHECKED_OUT | 240 |
| bk-04 | Núria | 302 | 01/10 a 03/10 | CHECKED_OUT | 440 |
| bk-05 | Pau | 202 | 05/10 a 07/10 | CHECKED_OUT | 180 |
| bk-06 | Anna | 102 | 10/10 a 12/10 | CHECKED_OUT | 120 |
| bk-07 | Marc | 203 | 15/10 a 18/10 | CHECKED_OUT | 330 |
| bk-08 | Marc | 103 | 15/11 a 18/11 | CHECKED_IN | 210 |
| bk-09 | Laia | 203 | 16/11 a 19/11 | CHECKED_IN | 330 |
| bk-10 | Núria | 201 | 01/12 a 05/12 | CONFIRMED | 360 |
| bk-11 | Anna | 301 | 10/12 a 12/12 | CONFIRMED | 360 |
| bk-12 | Pau | 202 | 01/12 a 04/12 | CANCELLED | 270 |

Totes les dates són del 2026.

## Endpoints que ja funcionen

| Mètode | Endpoint | Què fa |
|---|---|---|
| GET | /rooms | Llista totes les habitacions |
| GET | /rooms/{id} | Una habitació |
| POST | /rooms | Crea una habitació (201) |
| GET | /guests | Llista tots els clients |
| GET | /guests/{id} | Un client |
| POST | /guests | Crea un client (201) |
| GET | /bookings | Llista totes les reserves |
| GET | /bookings/{id} | Una reserva |
| POST | /bookings | Crea una reserva (201 + capçalera Location) |
| POST | /bookings/{id}/check-in | Entrada del client |
| POST | /bookings/{id}/check-out | Sortida del client |
| POST | /bookings/{id}/cancel | Cancel·la una reserva |

## Errors

| Situació | Codi |
|---|---|
| Recurs que no existeix (habitació, client, reserva) | **404 Not Found** |
| Email o número d'habitació repetit | **409 Conflict** |
| Petició incorrecta o regla de negoci incomplerta (habitació no disponible, dates incorrectes, canvi d'estat no permès, reserva solapada) | **400 Bad Request** |
| Error inesperat | **500 Internal Server Error** |

Llança `NotFoundException`, `ConflictException` o `BadRequestException` (paquet `exception`) i el `GlobalExceptionHandler` posa el codi correcte. Una `IllegalArgumentException` del domini també acaba en 400.

---

## Els tests són el contracte

> **Abans de programar un ticket, obre la seva classe de test.** El comentari de dalt explica quines dades de `data.sql` fa servir i els tests diuen exactament quina URL criden, quins paràmetres envien i quins camps del JSON comproven. És una especificació més precisa que el text d'aquest README.

Coses que els tests comproven al peu de la lletra:

- **Les rutes noves:** `GET /rooms/search`, `GET /guests/{id}/history` i `GET /reports/revenue`. Una ruta mal escrita dona 404 i el test falla.
- **Els noms dels camps del JSON.** Per als tickets #5 i #6 els DTOs ja estan fets (`GuestHistoryDTO` i `RoomTypeRevenueDTO`, a `application/dto`): fes-los servir tal com són.
- **Els valors per defecte:** a la cerca d'habitacions, si no s'envia `status`, val `AVAILABLE`.
- **El missatge d'error** del ticket #4.

Per executar només els tests d'un ticket: fletxa verda al costat del nom de la classe a IntelliJ, o bé `mvn test -Dtest=Ticket2_RoomSearchTest`.

💡 A `request.http` tens una petició preparada per a cada endpoint i cada ticket. Obre'l a IntelliJ amb l'aplicació engegada i prova l'API a mà mentre treballes.

Els tickets #1 a #4 diuen on s'ha de fer el canvi i el codi té un `TODO` en el lloc exacte. Als tickets #5 i #6 tens la regla de negoci i la resposta esperada: **decidir quins mètodes i quines capes necessites forma part de la prova.**

---

### TICKET #1 · Error: el descompte VIP no s'aplica

**Tipus:** error · **Dificultat:** ★☆☆

> *"Vam prometre un 10% de descompte als clients SILVER i un 15% als GOLD, però tothom paga el preu sencer!"*

**L'error:** `BookingService.createBooking()` calcula `totalPrice = nits × preu per nit` i no aplica cap descompte.

**Què cal fer:**

- STANDARD: 0% de descompte
- SILVER: 10% de descompte
- GOLD: 15% de descompte
- Fórmula: `totalPrice = nits × pricePerNight × (1 - descompte)`

**On:** `BookingService.createBooking()`

---

### TICKET #2 · Funcionalitat nova: cerca d'habitacions amb filtres

**Tipus:** endpoint nou · **Dificultat:** ★★☆

> *"A recepció volen buscar habitacions per tipus i per preu sense haver de mirar la llista sencera."*

**Endpoint nou:** `GET /rooms/search`

**Paràmetres** (tots opcionals i combinables):

| Paràmetre | Filtre |
|---|---|
| `type` | SINGLE, DOUBLE o SUITE |
| `maxPrice` | habitacions amb `pricePerNight` ≤ maxPrice |
| `status` | estat de l'habitació. **Si no s'envia, val AVAILABLE** |

**Retorna:** una llista de `RoomDTO`.

**Exemples:** `/rooms/search?type=DOUBLE` · `/rooms/search?type=SINGLE&maxPrice=60` · `/rooms/search?status=MAINTENANCE`

**On:**

- `RoomRestController`: l'endpoint nou (hi ha un `TODO`)
- `RoomService`: un mètode nou
- `RoomRepository`: una consulta amb SQL dinàmic (només afegeixes al `WHERE` els filtres que arriben informats)

També és vàlid llegir totes les habitacions i filtrar-les amb Streams al servei.

---

### TICKET #3 · Canvi: check-out amb l'estat de l'habitació

**Tipus:** modificació · **Dificultat:** ★★☆

> *"Quan un client marxa, el personal de neteja revisa l'habitació i li posa una nota de 0 a 100. Si està malament, no la podem tornar a llogar fins que la reparem. I si està molt malament, el client paga una nit més."*

**Canvi a:** `POST /bookings/{id}/check-out`

**Body nou (opcional):**

```json
{
  "roomCondition": 85
}
```

**Regles:**

- Es guarda `roomCondition` a l'habitació (camp `condition`).
- `roomCondition` ≥ 50: l'habitació torna a **AVAILABLE**, com ara.
- `roomCondition` < 50: l'habitació passa a **MAINTENANCE**.
- `roomCondition` < 30: l'habitació passa a **MAINTENANCE** i, a més, es cobra **una nit més**: `totalPrice = totalPrice + pricePerNight` de l'habitació.
- **Sense body**, el check-out funciona igual que ara (l'habitació torna a AVAILABLE i no es cobra res més).

**⚠️ Tècnica: un body opcional amb `@RequestBody(required = false)`.** El controlador ja està preparat:

```java
@PostMapping("/{id}/check-out")
public BookingDTO checkOut(@PathVariable String id,
                           @RequestBody(required = false) CheckOutDTO checkOutDTO) {
    Integer roomCondition = (checkOutDTO != null) ? checkOutDTO.roomCondition() : null;
    return bookingService.checkOut(id, roomCondition);
}
```

Amb `required = false`, quan arriba un body Spring omple `checkOutDTO`, i quan no n'arriba cap el deixa a `null` en lloc de respondre 400. **No el treguis:** si ho fas, els check-outs sense body deixaran de funcionar i fallarà `ExistingFeatureTest`. Per això `roomCondition` és un `Integer` i no un `int`: pot ser `null`.

**On:** `BookingService.checkOut()` (hi ha un `TODO` amb els passos)

---

### TICKET #4 · Error: es poden reservar dates que ja estan ocupades

**Tipus:** error · **Dificultat:** ★★☆

> *"Dues persones han reservat la mateixa habitació per a les mateixes nits. Això no pot tornar a passar!"*

**L'error:** `BookingService.createBooking()` no comprova si l'habitació ja està reservada per a aquelles dates.

**Què cal fer:**

- Abans de crear la reserva, busca les reserves **CONFIRMED** o **CHECKED_IN** de la mateixa habitació.
- Dues estades se solapen quan `existingCheckIn < newCheckOut` **i** `existingCheckOut > newCheckIn`.
- Si se solapa amb alguna: **400** amb el missatge exacte `"Room is already booked for the requested dates"`.
- Entrar el mateix dia que surt l'altre client està permès (sortida 05/12, entrada 05/12).
- Les reserves CANCELLED i CHECKED_OUT no bloquegen res.

**On:** `BookingService.createBooking()` (hi ha un `TODO`). Necessitaràs les reserves d'una habitació: pots afegir un mètode nou al `BookingRepository` (copia el patró de `findAll`) o filtrar `findAll()` amb Streams.

---

### TICKET #5 · Funcionalitat nova: historial d'un client

**Tipus:** endpoint nou · **Dificultat:** ★★☆

> *"Volem veure d'un cop d'ull totes les reserves d'un client i quant s'ha gastat amb nosaltres."*

**Endpoint nou:** `GET /guests/{id}/history`

**Resposta** (`GuestHistoryDTO`, ja creat):

```json
{
  "guest": { "id": "guest-anna", "name": "Anna Puig", "email": "...", "phone": "...", "vipLevel": "STANDARD" },
  "totalSpent": 390.0,
  "totalStays": 2,
  "bookings": [ { "id": "bk-11", ... }, { "id": "bk-06", ... }, { "id": "bk-01", ... } ]
}
```

**Regles:**

- `totalSpent`: suma del `totalPrice` de les reserves **CHECKED_OUT** del client.
- `totalStays`: quantes reserves **CHECKED_OUT** té.
- `bookings`: **totes** les reserves del client (qualsevol estat), ordenades per `checkIn` de la més nova a la més antiga.
- Si el client no existeix: **404**.
- Un client sense reserves: 200 amb `totalSpent` 0, `totalStays` 0 i `bookings` buida.

---

### TICKET #6 · Funcionalitat nova: informe d'ingressos per tipus d'habitació

**Tipus:** endpoint nou · **Dificultat:** ★★★

> *"La gerència vol saber quin tipus d'habitació ens fa guanyar més diners."*

**Endpoint nou:** `GET /reports/revenue`

**Resposta** (llista de `RoomTypeRevenueDTO`, ja creat):

```json
[
  { "roomType": "SUITE",  "totalRooms": 3, "checkedOutBookings": 2, "totalRevenue": 980.0, "averageBookingPrice": 490.0 },
  { "roomType": "DOUBLE", "totalRooms": 4, "checkedOutBookings": 3, "totalRevenue": 780.0, "averageBookingPrice": 260.0 },
  { "roomType": "SINGLE", "totalRooms": 3, "checkedOutBookings": 2, "totalRevenue": 360.0, "averageBookingPrice": 180.0 }
]
```

**Regles:**

- Una fila per cada tipus d'habitació (SINGLE, DOUBLE i SUITE), sempre les tres.
- `totalRooms`: quantes habitacions d'aquest tipus hi ha.
- `checkedOutBookings`: quantes reserves **CHECKED_OUT** hi ha d'habitacions d'aquest tipus.
- `totalRevenue`: suma del `totalPrice` d'aquestes reserves CHECKED_OUT.
- `averageBookingPrice`: `totalRevenue / checkedOutBookings`, o 0 si no n'hi ha cap.
- Ordenat per `totalRevenue` de més gran a més petit.

Aquest endpoint no encaixa en cap dels controladors que hi ha: pots crear-ne un de nou, amb el seu servei.

---

## Temps i estratègia

90 minuts per a 6 tickets és just, i està fet a propòsit. Cada test que passa suma, així que primer ves a buscar els punts fàcils de tots els tickets i després acaba els difícils.

| Ticket | Temps orientatiu |
|---|---|
| #1 Descompte VIP | 5 a 10 min |
| #4 Reserves solapades | 10 a 15 min |
| #3 Check-out amb estat | 10 a 15 min |
| #2 Cerca d'habitacions | 15 min |
| #5 Historial del client | 15 a 20 min |
| #6 Informe d'ingressos | 20 a 25 min |

## Puntuació

| Ticket | Tipus | Tests | Punts |
|---|---|---|---|
| #1 Descompte VIP | Error | 3 | 3 |
| #2 Cerca d'habitacions | Endpoint nou | 3 | 3 |
| #3 Check-out amb estat | Modificació | 3 | 3 |
| #4 Reserves solapades | Error | 3 | 3 |
| #5 Historial del client | Endpoint nou | 3 | 3 |
| #6 Informe d'ingressos | Endpoint nou | 3 | 3 |
| **TOTAL** | | **18** | **18** |

**Nota = (tests de tickets que passen / 18) × 10**

`ExistingFeatureTest` no suma: si algun d'aquests tests falla, vol dir que has trencat una cosa que funcionava, i es tindrà en compte a la revisió.

## Normes

1. **No modifiquis res de `src/test`** ni el fitxer `data.sql`.
2. Pots afegir classes, mètodes i DTOs nous, i modificar el codi de `src/main`.
3. Respecta les capes: controlador → servei → repositori. Un controlador no parla amb un repositori i l'SQL només va als repositoris. A la revisió posterior es mirarà el codi, no només els tests.
4. Posa el teu nom a `student-info.json`.
5. Fes commit i push sovint. Compta l'últim push abans que s'acabi el temps.

## Com executar-ho

```bash
# Engegar l'aplicació (o executar TecnoHostalApplication des d'IntelliJ)
mvn spring-boot:run

# Tots els tests
mvn test

# Només els tests d'un ticket
mvn test -Dtest=Ticket3_CheckOutConditionTest
```

Consola d'H2: http://localhost:8080/h2-console · JDBC URL `jdbc:h2:mem:tecnohostal` · usuari `sa` · sense contrasenya.

## Consells

- Comença pel ticket #1: és el més ràpid i et serveix per escalfar.
- Els tickets #1, #3 i #4 toquen `BookingService`: llegeix-lo sencer abans de començar.
- Persistència: `save(x)` fa un INSERT d'un objecte nou i `update(x)` fa un UPDATE de tots els camps que poden canviar. Per modificar una cosa, canvia l'objecte amb els seus setters i crida `update`.
- Per passar d'entitat a DTO tens `RoomDTO.from(...)`, `GuestDTO.from(...)` i `BookingDTO.from(...)`.
- Executa `ExistingFeatureTest` de tant en tant per comprovar que no has trencat res.
- Si un test falla, llegeix el missatge: diu quin camp esperava i quin valor ha trobat.

Bona sort!
