package com.hotel.service;

import com.hotel.dao.RoomDAO;
import com.hotel.exception.ValidationException;
import com.hotel.model.Room;
import com.hotel.model.RoomStatus;
import com.hotel.model.RoomType;
import com.hotel.util.InputValidator;

import java.math.BigDecimal;
import java.util.List;

public class RoomService {

    private final RoomDAO roomDAO = new RoomDAO();

    public void addRoom(String roomNumber, RoomType type, BigDecimal price) {
        InputValidator.requireNonEmpty(roomNumber, "Room number");
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price per night must be greater than zero.");
        }
        Room room = new Room(0, roomNumber, type, price, RoomStatus.AVAILABLE);
        roomDAO.addRoom(room);
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailableRooms();
    }

    public List<Room> searchByRoomNumber(String roomNumber) {
        return roomDAO.searchByRoomNumber(roomNumber);
    }

    public Room getRoomById(int roomId) {
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) throw new ValidationException("No room found with ID " + roomId);
        return room;
    }

    public boolean isAvailable(int roomId) {
        Room room = getRoomById(roomId);
        return room.getStatus() == RoomStatus.AVAILABLE;
    }

    public void updateRoom(Room room) {
        boolean updated = roomDAO.updateRoom(room);
        if (!updated) throw new ValidationException("Room not found. Update failed.");
    }

    public void deleteRoom(int roomId) {
        boolean deleted = roomDAO.deleteRoom(roomId);
        if (!deleted) throw new ValidationException("Room not found. Delete failed.");
    }
}
