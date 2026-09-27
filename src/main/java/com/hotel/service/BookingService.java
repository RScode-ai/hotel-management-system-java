package com.hotel.service;

import com.hotel.config.DatabaseConnection;
import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.exception.BookingException;
import com.hotel.exception.DatabaseException;
import com.hotel.exception.ValidationException;
import com.hotel.model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestService guestService = new GuestService();
    private final RoomService roomService = new RoomService();

    /**
     * Creates a booking. Insert + room status update happen in a single JDBC transaction:
     * if either step fails, the transaction is rolled back so data stays consistent.
     */
    public Booking createBooking(int guestId, int roomId, LocalDate checkIn, LocalDate checkOut, int numberOfGuests) {
        guestService.getGuestById(guestId); // throws if guest does not exist
        Room room = roomService.getRoomById(roomId); // throws if room does not exist

        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("Check-out date must be after check-in date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ValidationException("Check-in date cannot be in the past.");
        }
        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new BookingException("Room " + room.getRoomNumber() + " is under maintenance.");
        }
        if (bookingDAO.isRoomOccupiedForDates(roomId, checkIn, checkOut)) {
            throw new BookingException("Room " + room.getRoomNumber() + " is already booked for those dates.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking(0, guestId, roomId, checkIn, checkOut, numberOfGuests,
                BookingStatus.CONFIRMED, totalAmount);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int bookingId = bookingDAO.createBooking(booking, conn);
            roomDAO.updateRoomStatus(roomId, RoomStatus.OCCUPIED, conn);

            conn.commit();
            booking.setBookingId(bookingId);
            return booking;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DatabaseException("Failed to create booking. Transaction rolled back.", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public Booking getBookingById(int bookingId) {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) throw new ValidationException("No booking found with ID " + bookingId);
        return booking;
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    public List<Booking> getBookingsByGuestId(int guestId) {
        return bookingDAO.getBookingsByGuestId(guestId);
    }

    public void cancelBooking(int bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("Only CONFIRMED bookings can be cancelled.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            bookingDAO.updateBookingStatus(bookingId, BookingStatus.CANCELLED, conn);
            roomDAO.updateRoomStatus(booking.getRoomId(), RoomStatus.AVAILABLE, conn);

            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DatabaseException("Failed to cancel booking. Transaction rolled back.", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public void checkIn(int bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("Booking must be CONFIRMED to check in. Current status: " + booking.getBookingStatus());
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            bookingDAO.updateBookingStatus(bookingId, BookingStatus.CHECKED_IN, conn);
            roomDAO.updateRoomStatus(booking.getRoomId(), RoomStatus.OCCUPIED, conn);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check in.", e);
        }
    }

    public Booking checkOut(int bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBookingStatus() != BookingStatus.CHECKED_IN) {
            throw new BookingException("Booking must be CHECKED_IN to check out. Current status: " + booking.getBookingStatus());
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            bookingDAO.updateBookingStatus(bookingId, BookingStatus.CHECKED_OUT, conn);
            roomDAO.updateRoomStatus(booking.getRoomId(), RoomStatus.AVAILABLE, conn);

            conn.commit();
            booking.setBookingStatus(BookingStatus.CHECKED_OUT);
            return booking;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DatabaseException("Failed to check out. Transaction rolled back.", e);
        } finally {
            closeQuietly(conn);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) { }
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) { }
        }
    }
}
