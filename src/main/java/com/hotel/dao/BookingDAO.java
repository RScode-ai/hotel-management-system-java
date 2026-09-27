package com.hotel.dao;

import com.hotel.exception.DatabaseException;
import com.hotel.model.Booking;
import com.hotel.model.BookingStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    /** Inserts a booking using the given connection (part of a caller-managed transaction) and returns generated ID. */
    public int createBooking(Booking booking, Connection conn) throws SQLException {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, " +
                "number_of_guests, booking_status, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getGuestId());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, Date.valueOf(booking.getCheckInDate()));
            ps.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            ps.setInt(5, booking.getNumberOfGuests());
            ps.setString(6, booking.getBookingStatus().name());
            ps.setBigDecimal(7, booking.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            throw new SQLException("Booking insert did not return a generated ID.");
        }
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = com.hotel.config.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch booking.", e);
        }
    }

    public List<Booking> getAllBookings() {
        String sql = "SELECT * FROM bookings ORDER BY booking_id";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = com.hotel.config.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) bookings.add(mapRow(rs));
            return bookings;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch bookings.", e);
        }
    }

    public List<Booking> getBookingsByGuestId(int guestId) {
        String sql = "SELECT * FROM bookings WHERE guest_id = ? ORDER BY booking_id";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = com.hotel.config.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bookings.add(mapRow(rs));
            }
            return bookings;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch bookings for guest.", e);
        }
    }

    public boolean isRoomOccupiedForDates(int roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND booking_status IN ('CONFIRMED','CHECKED_IN') " +
                "AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = com.hotel.config.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check room availability.", e);
        }
    }

    public boolean updateBookingStatus(int bookingId, BookingStatus status, Connection conn) throws SQLException {
        String sql = "UPDATE bookings SET booking_status = ? WHERE booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("booking_id"),
                rs.getInt("guest_id"),
                rs.getInt("room_id"),
                rs.getDate("check_in_date").toLocalDate(),
                rs.getDate("check_out_date").toLocalDate(),
                rs.getInt("number_of_guests"),
                BookingStatus.valueOf(rs.getString("booking_status")),
                rs.getBigDecimal("total_amount")
        );
    }
}
