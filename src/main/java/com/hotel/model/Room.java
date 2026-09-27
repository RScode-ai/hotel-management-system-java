package com.hotel.model;

import java.math.BigDecimal;

public class Room {
    private int roomId;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal pricePerNight;
    private RoomStatus status;

    public Room() {}

    public Room(int roomId, String roomNumber, RoomType roomType, BigDecimal pricePerNight, RoomStatus status) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("ID:%-5d | Room:%-6s | Type:%-8s | Price:%-10s | Status:%s",
                roomId, roomNumber, roomType, pricePerNight, status);
    }
}
