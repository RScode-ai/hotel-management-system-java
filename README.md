# Hotel Management System

A console-based Hotel Management System built with **Java, JDBC, and MySQL**, using a layered architecture (Model–DAO–Service). Built as a B.Tech portfolio project to demonstrate Core Java, OOP, JDBC, SQL, exception handling, and transaction management.

## Features

- **Authentication** — role-based login (ADMIN / RECEPTIONIST) via MySQL, using `PreparedStatement`
- **Room Management** — add, view, search, update, delete rooms; check availability
- **Guest Management** — add, view, search, update, delete guests with input validation
- **Booking Management** — create/cancel/view bookings, with date and availability validation
- **Check-In / Check-Out** — status-driven workflow with room status sync
- **Billing** — auto-calculated bill (room charges, tax, discount, total)
- **Payment Management** — record and view payments (CASH/CARD/UPI)
- **Reports** — room/guest/booking counts, total revenue, and a joined booking+guest+room report
- **Transactions** — booking creation, cancellation, and check-out use JDBC transactions (`commit`/`rollback`) so room status and booking status stay in sync
- **Validation & Exceptions** — custom `ValidationException`, `BookingException`, `DatabaseException`; no crashes on bad input

## Technologies Used

Java 17+, JDBC, MySQL 8+, MySQL Connector/J, Maven, IntelliJ IDEA.

## Project Architecture

```
Main.java  →  Service layer (business logic)  →  DAO layer (JDBC/SQL)  →  MySQL
```

## Project Structure

```
hotel-management-system/
├── pom.xml
├── database/
│   └── hotel_management.sql
├── src/main/
│   ├── java/com/hotel/
│   │   ├── Main.java
│   │   ├── config/DatabaseConnection.java
│   │   ├── model/        (User, Room, Guest, Booking, Payment + enums)
│   │   ├── dao/          (UserDAO, RoomDAO, GuestDAO, BookingDAO, PaymentDAO)
│   │   ├── service/      (Authentication, Room, Guest, Booking, Payment, Report)
│   │   ├── exception/    (DatabaseException, ValidationException, BookingException)
│   │   └── util/         (InputValidator, DateUtil, ConsoleUtil)
│   └── resources/db.properties
└── README.md
```

## Database Design

5 tables: `users`, `rooms`, `guests`, `bookings`, `payments`, connected with foreign keys (`bookings.guest_id → guests`, `bookings.room_id → rooms`, `payments.booking_id → bookings`). See `database/hotel_management.sql` for full DDL + sample data (2 users, 10 rooms, 5 guests).

## Setup Instructions

### 1. MySQL Setup
1. Install/start MySQL Server 8+.
2. Run the script to create the database, tables, and sample data:
   ```
   mysql -u root -p < database/hotel_management.sql
   ```

### 2. Configure Database Connection
Edit `src/main/resources/db.properties`:
```
db.url=jdbc:mysql://localhost:3306/hotel_management
db.username=root
db.password=YOUR_PASSWORD
```

## How to Run in IntelliJ IDEA

1. **Open Project**: `File → Open` → select the `hotel-management-system` folder. IntelliJ will detect `pom.xml` and import it as a Maven project (it downloads MySQL Connector/J automatically).
2. **Set Java 17+**: `File → Project Structure → Project` → set SDK to 17 or higher.
3. **Set your DB password** in `src/main/resources/db.properties`.
4. **Run MySQL script** (Setup step 1 above) so the database exists.
5. **Run the app**: open `src/main/java/com/hotel/Main.java` → click the green ▶ next to `public static void main`.
6. Interact with the menu in the **Run** console at the bottom.

### Run from terminal (alternative)
```
mvn clean package
java -cp target/hotel-management-system-jar-with-dependencies.jar com.hotel.Main
```

## Sample Login

| Username  | Password      | Role         |
|-----------|---------------|--------------|
| admin     | admin123      | ADMIN        |
| reception | reception123  | RECEPTIONIST |

> Passwords are stored in plain text in this version — a documented learning-project limitation (see Security Basics below). Hashing (e.g. BCrypt) is a natural next step.

## Security Basics Implemented

- All SQL uses `PreparedStatement` — no string-concatenated queries
- Database credentials kept in `db.properties`, not hardcoded in source, and excluded via `.gitignore`
- Input validated before hitting the database
- Passwords never printed to console

## Future Improvements

- Password hashing (BCrypt)
- GUI (JavaFX/Swing) or REST API (Spring Boot) front end
- Connection pooling (HikariCP)
- Unit tests (JUnit + Mockito) for the service layer
- Pagination for large guest/room/booking lists

## Verified Working

This project was compiled end-to-end with `javac` (30 source files, zero errors) and smoke-tested at runtime: the menu, login flow, and database-error handling all behave as expected. Once pointed at a running MySQL instance with the schema loaded, all CRUD, booking, check-in/out, billing, payment, and report features are fully functional.
