package com.hotel.dao;

import com.hotel.config.DatabaseConnection;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Payment;
import com.hotel.model.PaymentMethod;
import com.hotel.model.PaymentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public void createPayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, payment_status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getBookingId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMethod().name());
            ps.setString(4, payment.getPaymentStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record payment.", e);
        }
    }

    public Payment getPaymentById(int paymentId) {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payment.", e);
        }
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ? ORDER BY payment_id";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) payments.add(mapRow(rs));
            }
            return payments;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payments.", e);
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getInt("payment_id"),
                rs.getInt("booking_id"),
                rs.getBigDecimal("amount"),
                PaymentMethod.valueOf(rs.getString("payment_method")),
                PaymentStatus.valueOf(rs.getString("payment_status")),
                rs.getTimestamp("payment_date").toLocalDateTime()
        );
    }
}
