package com.hotel.dao;

import com.hotel.config.DatabaseConnection;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Room;
import com.hotel.model.RoomStatus;
import com.hotel.model.RoomType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType().name());
            ps.setBigDecimal(3, room.getPricePerNight());
            ps.setString(4, room.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add room.", e);
        }
    }

    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
            return rooms;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch rooms.", e);
        }
    }

    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch room.", e);
        }
    }

    public List<Room> searchByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number LIKE ?";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + roomNumber + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rooms.add(mapRow(rs));
            }
            return rooms;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search rooms.", e);
        }
    }

    public List<Room> getAvailableRooms() {
        String sql = "SELECT * FROM rooms WHERE status = 'AVAILABLE' ORDER BY room_id";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rooms.add(mapRow(rs));
            return rooms;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch available rooms.", e);
        }
    }

    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, price_per_night = ?, status = ? WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType().name());
            ps.setBigDecimal(3, room.getPricePerNight());
            ps.setString(4, room.getStatus().name());
            ps.setInt(5, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update room.", e);
        }
    }

    public boolean updateRoomStatus(int roomId, RoomStatus status, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete room. It may have existing bookings.", e);
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("room_id"),
                rs.getString("room_number"),
                RoomType.valueOf(rs.getString("room_type")),
                rs.getBigDecimal("price_per_night"),
                RoomStatus.valueOf(rs.getString("status"))
        );
    }
}
