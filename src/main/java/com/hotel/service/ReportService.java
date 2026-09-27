package com.hotel.service;

import com.hotel.config.DatabaseConnection;
import com.hotel.exception.DatabaseException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {

    public Map<String, String> getSummaryReport() {
        Map<String, String> report = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            report.put("Total Rooms", String.valueOf(count(conn, "SELECT COUNT(*) FROM rooms")));
            report.put("Available Rooms", String.valueOf(count(conn, "SELECT COUNT(*) FROM rooms WHERE status='AVAILABLE'")));
            report.put("Occupied Rooms", String.valueOf(count(conn, "SELECT COUNT(*) FROM rooms WHERE status='OCCUPIED'")));
            report.put("Total Guests", String.valueOf(count(conn, "SELECT COUNT(*) FROM guests")));
            report.put("Total Bookings", String.valueOf(count(conn, "SELECT COUNT(*) FROM bookings")));
            report.put("Total Revenue", "₹" + sum(conn, "SELECT COALESCE(SUM(amount),0) FROM payments WHERE payment_status='PAID'"));
            return report;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to generate report.", e);
        }
    }

    /** Booking + Guest + Room joined view, demonstrating a multi-table JOIN. */
    public void printBookingDetailsReport() {
        String sql = "SELECT b.booking_id, g.first_name, g.last_name, r.room_number, r.room_type, " +
                "b.check_in_date, b.check_out_date, b.booking_status, b.total_amount " +
                "FROM bookings b " +
                "JOIN guests g ON b.guest_id = g.guest_id " +
                "JOIN rooms r ON b.room_id = r.room_id " +
                "ORDER BY b.booking_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.printf("Booking#%-4d | %s %s | Room %s (%s) | %s to %s | %s | Amount:%s%n",
                        rs.getInt("booking_id"), rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("room_number"), rs.getString("room_type"),
                        rs.getDate("check_in_date"), rs.getDate("check_out_date"),
                        rs.getString("booking_status"), rs.getBigDecimal("total_amount"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to generate booking details report.", e);
        }
    }

    private int count(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private BigDecimal sum(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getBigDecimal(1);
        }
    }
}
