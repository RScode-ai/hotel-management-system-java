# Hotel Management System

This project was developed locally in IntelliJ IDEA for managing hotel operations using Java, JDBC, and MySQL. It is a console-based application for handling rooms, guests, bookings, payments, and reports.

The project was created as a practical learning exercise to strengthen Java programming, database connectivity, OOP principles, and transactional database workflows.

## Tech Stack

- Java 17
- Maven
- MySQL
- JDBC
- IntelliJ IDEA

## Features

- Admin and receptionist login
- Add, view, update, and delete rooms
- Add, view, update, and delete guests
- Create and track bookings
- Check room availability
- Check-in and check-out workflow
- Billing and payment handling
- Reports for rooms, guests, bookings, and revenue
- Transaction-based updates for booking-related operations

## Project Structure

```text
hotel-management-system/
├── pom.xml
├── README.md
├── .gitignore
├── database/
│   └── hotel_management.sql
├── src/
│   └── main/
│       ├── java/com/hotel/
│       │   ├── Main.java
│       │   ├── config/
│       │   ├── dao/
│       │   ├── exception/
│       │   ├── model/
│       │   ├── service/
│       │   └── util/
│       └── resources/
│           └── db.properties
└── target/
```

## Database Setup

1. Install and start MySQL on your machine.
2. Open MySQL terminal or MySQL Workbench.
3. Run the provided SQL script:

```bash
mysql -u root -p < database/hotel_management.sql
```

This script creates the `hotel_management` database and inserts sample data.

## IntelliJ IDEA Setup

1. Open IntelliJ IDEA.
2. Select `File -> Open` and choose this project folder.
3. Let Maven import the project automatically.
4. Ensure Java 17 is selected in the project SDK.
5. Update the database configuration in `src/main/resources/db.properties`.

Example:

```properties
db.url=jdbc:mysql://localhost:3306/hotel_management
db.username=root
db.password=your_mysql_password
```

## Run the Application

Run directly from IntelliJ:

1. Open `src/main/java/com/hotel/Main.java`
2. Click the Run button
3. Use the console menu to interact with the application

Optional terminal command:

```bash
mvn clean package
java -cp target/hotel-management-system-jar-with-dependencies.jar com.hotel.Main
```

## Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| reception | reception123 | RECEPTIONIST |

## Notes

- Database credentials are stored in `db.properties` and should not be shared publicly.
- This is a learning project created for Java and database practice.
- Passwords are currently stored in plain text in the sample database, which can be improved later with hashing.

## Future Improvements

- Add BCrypt password hashing
- Improve input validation and exception handling
- Add GUI support using JavaFX or Swing
- Add JUnit test cases
- Improve reporting and filtering features

This project is a local Java-based hotel management system built in IntelliJ IDEA and designed to demonstrate real-world database-driven application development.
