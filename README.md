# BusBuddy

BusBuddy is a Spring Boot backend for bus booking. It supports two personas:
- Providers: manage buses, create and maintain schedules, and view/cancel bookings on their routes.
- Users: browse schedules and book seats.

The application uses:
- Java 17
- Spring Boot 3.x (Web, Security, Validation, Data JPA)
- JWT-based authentication
- MySQL for persistence
- Gradle build
- Lombok

## Tech Stack

- Java 17
- Spring Boot 3.5.x
    - spring-boot-starter-web
    - spring-boot-starter-data-jpa
    - spring-boot-starter-security
    - spring-boot-starter-validation
- JJWT (io.jsonwebtoken) for JWT tokens
- MySQL (Connector/J)
- Lombok

## Features

- Authentication (email/password) for both Users and Providers with JWT issuance.
- User management: create, read, update, delete users and providers.
- Provider flows:
    - Add/delete/list buses
    - Create/update/delete/list schedules
    - View all bookings on their schedules
    - Cancel a booking (as a provider)
- Booking flows:
    - Create booking by seatNumbers (preferred) or seatIds
    - Get booking by id
    - List my bookings
    - Cancel my booking (as a user)
- Concurrency-safe seat booking using pessimistic row locks to prevent double booking.

## Project Structure (high level)

- `src/main/java/com/application/busbuddy/controller`
    - `AuthController` — `/api/auth/login`
    - `UserController` — `/api/v1/users` (+ `/providers`)
    - `ProviderController` — `/api/v1/providers` (buses, schedules, provider-cancel bookings)
    - `BookingController` — `/api/v1/bookings`
- `src/main/java/com/application/busbuddy/model`
    - Core entities like `User`, `Provider`, `Bus`, `Schedule`, `Seat`, `Booking`, etc.
- `src/main/resources`
    - `application.properties` — environment configuration (see below)

## Prerequisites

- JDK 17+
- MySQL 8.x (running locally or accessible)
- Git
- Gradle wrapper (included)

Optional: run MySQL via Docker

```bash
docker run --name busbuddy-mysql -e MYSQL_ROOT_PASSWORD=yourpassword -e MYSQL_DATABASE=busbuddy -p 3306:3306 -d mysql:8
```

## Configuration

Create or update `src/main/resources/application.properties` with your MySQL settings:

```properties
# MySQL datasource
spring.datasource.url=jdbc:mysql://localhost:3306/busbuddy?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=yourpassword

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server (optional)
server.port=8080

# JWT (adjust keys to match your JwtUtil if different)
app.jwt.secret=change-me-super-secret-key
app.jwt.expiration-ms=86400000
```

Notes:
- Ensure the database `busbuddy` exists or use `createDatabaseIfNotExist=true` as above.
- If your `JwtUtil` expects different property names, align them accordingly.

## Build & Run

Using Gradle wrapper:

```bash
# from the project root
./gradlew clean build

# run the built jar
java -jar build/libs/busbuddy-0.0.1-SNAPSHOT.jar

# or run directly
./gradlew bootRun
```

Application will start on `http://localhost:8080` (unless you changed the port).

## Authentication

Obtain a JWT by POSTing credentials to `/api/auth/login`.

Request:
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password"
}
```

Response:
```json
{
  "token": "JWT_TOKEN_HERE",
  "role": "USER" // or "PROVIDER"
}
```

Use the token in subsequent requests:
```
Authorization: Bearer <JWT_TOKEN_HERE>
```

## API Overview

Below are the primary endpoints exposed by controllers. Payload shapes reflect current DTO usage.

### Users & Providers

- Create user or provider
```http
POST /api/v1/users
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "password",
  "role": "USER"   // or "PROVIDER"
}
```

- List all users
```http
GET /api/v1/users
```

- List all providers
```http
GET /api/v1/users/providers
```

- Get user or provider by id (role decides which resource is returned)
```http
GET /api/v1/users/{id}?role=USER
GET /api/v1/users/{id}?role=PROVIDER
```

- Update user or provider
```http
PUT /api/v1/users/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "email": "updated@example.com",
  "password": "newpassword",
  "role": "USER" // or "PROVIDER"
}
```

- Delete user or provider
```http
DELETE /api/v1/users/{id}?role=USER
DELETE /api/v1/users/{id}?role=PROVIDER
```

### Provider: Buses

- Add bus
```http
POST /api/v1/providers/buses
Authorization: Bearer <PROVIDER JWT>
Content-Type: application/json

{
  "busNumber": "BUS-1001",
  "busType": "AC",
  "totalSeats": 40
}
```

- Get buses
```http
GET /api/v1/providers/buses
Authorization: Bearer <PROVIDER JWT>
```

- Delete bus
```http
DELETE /api/v1/providers/buses/{busId}
Authorization: Bearer <PROVIDER JWT>
```

### Provider: Schedules

- Add schedule
```http
POST /api/v1/providers/schedules
Authorization: Bearer <PROVIDER JWT>
Content-Type: application/json

{
  "source": "Pune",
  "destination": "Mumbai",
  "departureTime": "08:00:00",      // HH:mm:ss
  "arrivalTime": "12:00:00",        // HH:mm:ss
  "travelDate": "2025-08-11",       // yyyy-MM-dd
  "busId": 7,
  "pricePerSeat": 450
}
```

- Update schedule
```http
PUT /api/v1/providers/schedules/{scheduleId}
Authorization: Bearer <PROVIDER JWT>
Content-Type: application/json

{
  "source": "Pune",
  "destination": "Mumbai",
  "departureTime": "09:00:00",
  "arrivalTime": "13:00:00",
  "travelDate": "2025-08-12",
  "busId": 7,
  "pricePerSeat": 500
}
```

- Delete schedule
```http
DELETE /api/v1/providers/schedules/{scheduleId}
Authorization: Bearer <PROVIDER JWT>
```

- List schedules for the provider
```http
GET /api/v1/providers/schedules
Authorization: Bearer <PROVIDER JWT>
```

### Provider: Bookings View/Cancel

- List all bookings for provider’s schedules
```http
GET /api/v1/providers/bookings
Authorization: Bearer <PROVIDER JWT>
```

- Cancel a booking (provider-initiated)
```http
DELETE /api/v1/providers/bookings/{bookingId}
Authorization: Bearer <PROVIDER JWT>
```

### Bookings (User)

- Create booking (preferred: by seatNumbers)
```http
POST /api/v1/bookings
Authorization: Bearer <USER JWT>
Content-Type: application/json

{
  "scheduleId": 123,
  "seatNumbers": ["Seat_1", "Seat_2"]
}
```

- Create booking (legacy: by seatIds)
```http
POST /api/v1/bookings
Authorization: Bearer <USER JWT>
Content-Type: application/json

{
  "scheduleId": 123,
  "seatIds": [456, 457]
}
```

- Get my booking
```http
GET /api/v1/bookings/{id}
Authorization: Bearer <USER JWT>
```

- List my bookings
```http
GET /api/v1/bookings
Authorization: Bearer <USER JWT>
```

- Cancel my booking
```http
PUT /api/v1/bookings/{id}/cancel
Authorization: Bearer <USER JWT>
```

## Booking Notes and Concurrency

- Seats are persisted per schedule as `Seat_1 ... Seat_N` based on the bus’s total seats.
- The service uses pessimistic locking when fetching seats for a booking to prevent double booking.
- If booking by `seatIds`, ensure the IDs belong to the same schedule.
- If booking by `seatNumbers`, ensure they exist for the schedule and are not already booked.

## Development Tips

- Lombok: Enable annotation processing in your IDE (IntelliJ IDEA → Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable).
- Use the Gradle tool window to run tasks like `bootRun`, `test`, and `build`.
- Turn on SQL logging (already shown in properties) during development to trace queries.

## Running Tests

```bash
./gradlew test
```

## License

This project is currently unlicensed. Add a LICENSE file if you plan to distribute it.

---
If you run into issues starting up with MySQL, verify:
- The DB is reachable and credentials are correct.
- The user has privileges to create/alter tables (if using `ddl-auto=update`).
- The correct MySQL dialect is set (MySQL8).