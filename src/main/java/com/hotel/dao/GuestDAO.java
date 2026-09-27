package com.hotel.dao;

import com.hotel.config.DatabaseConnection;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Guest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public void addGuest(Guest guest) {
        String sql = "INSERT INTO guests (first_name, last_name, phone, email, address, id_proof_type, id_proof_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindGuest(ps, guest);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add guest.", e);
        }
    }

    public List<Guest> getAllGuests() {
        String sql = "SELECT * FROM guests ORDER BY guest_id";
        List<Guest> guests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) guests.add(mapRow(rs));
            return guests;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch guests.", e);
        }
    }

    public Guest getGuestById(int guestId) {
        String sql = "SELECT * FROM guests WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch guest.", e);
        }
    }

    public List<Guest> searchByName(String name) {
        String sql = "SELECT * FROM guests WHERE first_name LIKE ? OR last_name LIKE ?";
        List<Guest> guests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) guests.add(mapRow(rs));
            }
            return guests;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search guests.", e);
        }
    }

    public boolean updateGuest(Guest guest) {
        String sql = "UPDATE guests SET first_name=?, last_name=?, phone=?, email=?, address=?, " +
                "id_proof_type=?, id_proof_number=? WHERE guest_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindGuest(ps, guest);
            ps.setInt(8, guest.getGuestId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update guest.", e);
        }
    }

    public boolean deleteGuest(int guestId) {
        String sql = "DELETE FROM guests WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete guest. They may have existing bookings.", e);
        }
    }

    private void bindGuest(PreparedStatement ps, Guest guest) throws SQLException {
        ps.setString(1, guest.getFirstName());
        ps.setString(2, guest.getLastName());
        ps.setString(3, guest.getPhone());
        ps.setString(4, guest.getEmail());
        ps.setString(5, guest.getAddress());
        ps.setString(6, guest.getIdProofType());
        ps.setString(7, guest.getIdProofNumber());
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        return new Guest(
                rs.getInt("guest_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("id_proof_type"),
                rs.getString("id_proof_number")
        );
    }
}
