package com.hotel;

import com.hotel.exception.BookingException;
import com.hotel.exception.DatabaseException;
import com.hotel.exception.ValidationException;
import com.hotel.model.*;
import com.hotel.service.*;
import com.hotel.util.ConsoleUtil;
import com.hotel.util.DateUtil;
import com.hotel.util.InputValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthenticationService authService = new AuthenticationService();
    private static final RoomService roomService = new RoomService();
    private static final GuestService guestService = new GuestService();
    private static final BookingService bookingService = new BookingService();
    private static final PaymentService paymentService = new PaymentService();
    private static final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        ConsoleUtil.printHeader("HOTEL MANAGEMENT SYSTEM");
        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                handleLogin();
            } else if (choice.equals("2")) {
                System.out.println("Goodbye!");
                break;
            } else {
                ConsoleUtil.printError("Invalid choice.");
            }
        }
        scanner.close();
    }

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user;
        try {
            user = authService.login(username, password);
        } catch (DatabaseException e) {
            ConsoleUtil.printError(e.getMessage());
            return;
        }

        if (user == null) {
            ConsoleUtil.printError("Invalid username or password.");
            return;
        }

        ConsoleUtil.printSuccess("Login successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        if (user.getRole() == UserRole.ADMIN) {
            adminDashboard();
        } else {
            receptionistDashboard();
        }
    }

    // ---------------- ADMIN DASHBOARD ----------------

    private static void adminDashboard() {
        while (true) {
            ConsoleUtil.printHeader("ADMIN DASHBOARD");
            System.out.println("1. Room Management");
            System.out.println("2. Guest Management");
            System.out.println("3. Booking Management");
            System.out.println("4. Payment Management");
            System.out.println("5. View Reports");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> roomManagementMenu();
                case "2" -> guestManagementMenu();
                case "3" -> bookingManagementMenu();
                case "4" -> paymentManagementMenu();
                case "5" -> reportsMenu();
                case "6" -> { return; }
                default -> ConsoleUtil.printError("Invalid choice.");
            }
        }
    }

    // ---------------- RECEPTIONIST DASHBOARD ----------------

    private static void receptionistDashboard() {
        while (true) {
            ConsoleUtil.printHeader("RECEPTIONIST DASHBOARD");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Guest Management");
            System.out.println("3. Create Booking");
            System.out.println("4. Check-In");
            System.out.println("5. Check-Out");
            System.out.println("6. Payment");
            System.out.println("7. View Booking");
            System.out.println("8. Logout");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> printRooms(roomService.getAvailableRooms());
                case "2" -> guestManagementMenu();
                case "3" -> createBookingFlow();
                case "4" -> checkInFlow();
                case "5" -> checkOutFlow();
                case "6" -> makePaymentFlow();
                case "7" -> viewBookingFlow();
                case "8" -> { return; }
                default -> ConsoleUtil.printError("Invalid choice.");
            }
        }
    }

    // ---------------- ROOM MANAGEMENT ----------------

    private static void roomManagementMenu() {
        while (true) {
            ConsoleUtil.printHeader("ROOM MANAGEMENT");
            System.out.println("1. Add Room");
            System.out.println("2. View Rooms");
            System.out.println("3. Search Room");
            System.out.println("4. Update Room");
            System.out.println("5. Delete Room");
            System.out.println("6. Available Rooms");
            System.out.println("7. Back");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> addRoomFlow();
                    case "2" -> printRooms(roomService.getAllRooms());
                    case "3" -> {
                        System.out.print("Enter room number to search: ");
                        printRooms(roomService.searchByRoomNumber(scanner.nextLine()));
                    }
                    case "4" -> updateRoomFlow();
                    case "5" -> {
                        System.out.print("Enter room ID to delete: ");
                        int id = InputValidator.parseInt(scanner.nextLine(), "room ID");
                        roomService.deleteRoom(id);
                        ConsoleUtil.printSuccess("Room deleted.");
                    }
                    case "6" -> printRooms(roomService.getAvailableRooms());
                    case "7" -> { return; }
                    default -> ConsoleUtil.printError("Invalid choice.");
                }
            } catch (ValidationException | DatabaseException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    private static void addRoomFlow() {
        System.out.print("Room number: ");
        String number = scanner.nextLine();
        RoomType type = readRoomType();
        System.out.print("Price per night: ");
        BigDecimal price = InputValidator.parseDecimal(scanner.nextLine(), "price");
        roomService.addRoom(number, type, price);
        ConsoleUtil.printSuccess("Room added.");
    }

    private static void updateRoomFlow() {
        System.out.print("Enter room ID to update: ");
        int id = InputValidator.parseInt(scanner.nextLine(), "room ID");
        Room room = roomService.getRoomById(id);
        System.out.print("New room number [" + room.getRoomNumber() + "]: ");
        String number = scanner.nextLine();
        if (!number.isBlank()) room.setRoomNumber(number);
        System.out.print("New price [" + room.getPricePerNight() + "] (blank to keep): ");
        String priceInput = scanner.nextLine();
        if (!priceInput.isBlank()) room.setPricePerNight(InputValidator.parseDecimal(priceInput, "price"));
        System.out.print("New status (AVAILABLE/OCCUPIED/MAINTENANCE) [" + room.getStatus() + "] (blank to keep): ");
        String statusInput = scanner.nextLine();
        if (!statusInput.isBlank()) room.setStatus(RoomStatus.valueOf(statusInput.trim().toUpperCase()));
        roomService.updateRoom(room);
        ConsoleUtil.printSuccess("Room updated.");
    }

    private static RoomType readRoomType() {
        while (true) {
            System.out.print("Room type (SINGLE/DOUBLE/DELUXE/SUITE): ");
            try {
                return RoomType.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                ConsoleUtil.printError("Invalid room type.");
            }
        }
    }

    private static void printRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }
        ConsoleUtil.printSeparator();
        rooms.forEach(System.out::println);
        ConsoleUtil.printSeparator();
    }

    // ---------------- GUEST MANAGEMENT ----------------

    private static void guestManagementMenu() {
        while (true) {
            ConsoleUtil.printHeader("GUEST MANAGEMENT");
            System.out.println("1. Add Guest");
            System.out.println("2. View Guests");
            System.out.println("3. Search Guest");
            System.out.println("4. Update Guest");
            System.out.println("5. Delete Guest");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> addGuestFlow();
                    case "2" -> printGuests(guestService.getAllGuests());
                    case "3" -> {
                        System.out.print("Enter name to search: ");
                        printGuests(guestService.searchByName(scanner.nextLine()));
                    }
                    case "4" -> updateGuestFlow();
                    case "5" -> {
                        System.out.print("Enter guest ID to delete: ");
                        int id = InputValidator.parseInt(scanner.nextLine(), "guest ID");
                        guestService.deleteGuest(id);
                        ConsoleUtil.printSuccess("Guest deleted.");
                    }
                    case "6" -> { return; }
                    default -> ConsoleUtil.printError("Invalid choice.");
                }
            } catch (ValidationException | DatabaseException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    private static void addGuestFlow() {
        System.out.print("First name: ");
        String first = scanner.nextLine();
        System.out.print("Last name: ");
        String last = scanner.nextLine();
        System.out.print("Phone (10 digits): ");
        String phone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("ID proof type (Aadhaar/PAN/Passport): ");
        String idType = scanner.nextLine();
        System.out.print("ID proof number: ");
        String idNumber = scanner.nextLine();

        Guest guest = new Guest(0, first, last, phone, email, address, idType, idNumber);
        guestService.addGuest(guest);
        ConsoleUtil.printSuccess("Guest added.");
    }

    private static void updateGuestFlow() {
        System.out.print("Enter guest ID to update: ");
        int id = InputValidator.parseInt(scanner.nextLine(), "guest ID");
        Guest guest = guestService.getGuestById(id);
        System.out.print("New phone [" + guest.getPhone() + "] (blank to keep): ");
        String phone = scanner.nextLine();
        if (!phone.isBlank()) guest.setPhone(phone);
        System.out.print("New email [" + guest.getEmail() + "] (blank to keep): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) guest.setEmail(email);
        System.out.print("New address [" + guest.getAddress() + "] (blank to keep): ");
        String address = scanner.nextLine();
        if (!address.isBlank()) guest.setAddress(address);
        guestService.updateGuest(guest);
        ConsoleUtil.printSuccess("Guest updated.");
    }

    private static void printGuests(List<Guest> guests) {
        if (guests.isEmpty()) {
            System.out.println("No guests found.");
            return;
        }
        ConsoleUtil.printSeparator();
        guests.forEach(System.out::println);
        ConsoleUtil.printSeparator();
    }

    // ---------------- BOOKING MANAGEMENT (Admin) ----------------

    private static void bookingManagementMenu() {
        while (true) {
            ConsoleUtil.printHeader("BOOKING MANAGEMENT");
            System.out.println("1. Create Booking");
            System.out.println("2. View Booking");
            System.out.println("3. View All Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> createBookingFlow();
                    case "2" -> viewBookingFlow();
                    case "3" -> printBookings(bookingService.getAllBookings());
                    case "4" -> {
                        System.out.print("Enter booking ID to cancel: ");
                        int id = InputValidator.parseInt(scanner.nextLine(), "booking ID");
                        bookingService.cancelBooking(id);
                        ConsoleUtil.printSuccess("Booking cancelled.");
                    }
                    case "5" -> { return; }
                    default -> ConsoleUtil.printError("Invalid choice.");
                }
            } catch (ValidationException | BookingException | DatabaseException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    private static void createBookingFlow() {
        try {
            System.out.print("Guest ID: ");
            int guestId = InputValidator.parseInt(scanner.nextLine(), "guest ID");
            System.out.print("Room ID: ");
            int roomId = InputValidator.parseInt(scanner.nextLine(), "room ID");
            System.out.print("Check-in date (yyyy-MM-dd): ");
            LocalDate checkIn = DateUtil.parseDate(scanner.nextLine());
            System.out.print("Check-out date (yyyy-MM-dd): ");
            LocalDate checkOut = DateUtil.parseDate(scanner.nextLine());
            System.out.print("Number of guests: ");
            int numGuests = InputValidator.parseInt(scanner.nextLine(), "number of guests");

            Booking booking = bookingService.createBooking(guestId, roomId, checkIn, checkOut, numGuests);
            ConsoleUtil.printSuccess("Booking created successfully!");
            System.out.println(booking);
        } catch (ValidationException | BookingException | DatabaseException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private static void viewBookingFlow() {
        try {
            System.out.print("Enter booking ID: ");
            int id = InputValidator.parseInt(scanner.nextLine(), "booking ID");
            System.out.println(bookingService.getBookingById(id));
        } catch (ValidationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private static void printBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        ConsoleUtil.printSeparator();
        bookings.forEach(System.out::println);
        ConsoleUtil.printSeparator();
    }

    // ---------------- CHECK-IN / CHECK-OUT ----------------

    private static void checkInFlow() {
        try {
            ConsoleUtil.printHeader("CHECK-IN");
            System.out.print("Booking ID: ");
            int id = InputValidator.parseInt(scanner.nextLine(), "booking ID");
            bookingService.checkIn(id);
            Booking booking = bookingService.getBookingById(id);
            Guest guest = guestService.getGuestById(booking.getGuestId());
            Room room = roomService.getRoomById(booking.getRoomId());
            System.out.println("\nGuest: " + guest.getFullName());
            System.out.println("Room: " + room.getRoomNumber());
            System.out.println("Check-in Date: " + DateUtil.format(booking.getCheckInDate()));
            ConsoleUtil.printSuccess("Check-in successful!");
        } catch (ValidationException | BookingException | DatabaseException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private static void checkOutFlow() {
        try {
            ConsoleUtil.printHeader("CHECK-OUT");
            System.out.print("Booking ID: ");
            int id = InputValidator.parseInt(scanner.nextLine(), "booking ID");
            Booking booking = bookingService.checkOut(id);
            printBill(booking);
        } catch (ValidationException | BookingException | DatabaseException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private static void printBill(Booking booking) {
        Guest guest = guestService.getGuestById(booking.getGuestId());
        Room room = roomService.getRoomById(booking.getRoomId());
        long nights = java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        BigDecimal roomCharges = booking.getTotalAmount();
        BigDecimal tax = roomCharges.multiply(BigDecimal.valueOf(0.12)).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal additionalCharges = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal total = roomCharges.add(tax).add(additionalCharges).subtract(discount);

        ConsoleUtil.printHeader("HOTEL BILL");
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Guest Name: " + guest.getFullName());
        System.out.println("Room Number: " + room.getRoomNumber());
        System.out.println("Room Type: " + room.getRoomType());
        System.out.println("Check-in: " + DateUtil.format(booking.getCheckInDate()));
        System.out.println("Check-out: " + DateUtil.format(booking.getCheckOutDate()));
        System.out.println("Number of Nights: " + nights);
        System.out.println("Price Per Night: " + room.getPricePerNight());
        ConsoleUtil.printSeparator();
        System.out.println("Room Charges: " + roomCharges);
        System.out.println("Additional Charges: " + additionalCharges);
        System.out.println("Tax (12%): " + tax);
        System.out.println("Discount: " + discount);
        ConsoleUtil.printSeparator();
        System.out.println("TOTAL AMOUNT: " + total);
    }

    // ---------------- PAYMENT MANAGEMENT ----------------

    private static void paymentManagementMenu() {
        while (true) {
            ConsoleUtil.printHeader("PAYMENT MANAGEMENT");
            System.out.println("1. Make Payment");
            System.out.println("2. View Payment");
            System.out.println("3. Search Payments by Booking");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> makePaymentFlow();
                    case "2" -> {
                        System.out.print("Enter payment ID: ");
                        int id = InputValidator.parseInt(scanner.nextLine(), "payment ID");
                        System.out.println(paymentService.getPaymentById(id));
                    }
                    case "3" -> {
                        System.out.print("Enter booking ID: ");
                        int id = InputValidator.parseInt(scanner.nextLine(), "booking ID");
                        paymentService.getPaymentsByBookingId(id).forEach(System.out::println);
                    }
                    case "4" -> { return; }
                    default -> ConsoleUtil.printError("Invalid choice.");
                }
            } catch (ValidationException | DatabaseException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    private static void makePaymentFlow() {
        try {
            System.out.print("Booking ID: ");
            int bookingId = InputValidator.parseInt(scanner.nextLine(), "booking ID");
            System.out.print("Payment method (CASH/CARD/UPI): ");
            PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().trim().toUpperCase());
            Payment payment = paymentService.makePayment(bookingId, method);
            ConsoleUtil.printSuccess("Payment recorded successfully.");
            System.out.println(payment);
        } catch (ValidationException | DatabaseException | IllegalArgumentException e) {
            ConsoleUtil.printError(e.getMessage() != null ? e.getMessage() : "Invalid payment method.");
        }
    }

    // ---------------- REPORTS ----------------

    private static void reportsMenu() {
        ConsoleUtil.printHeader("REPORTS");
        try {
            Map<String, String> summary = reportService.getSummaryReport();
            summary.forEach((k, v) -> System.out.printf("%-20s: %s%n", k, v));
            ConsoleUtil.printSeparator();
            System.out.println("Booking Details (Guest + Room joined):");
            reportService.printBookingDetailsReport();
        } catch (DatabaseException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }
}
