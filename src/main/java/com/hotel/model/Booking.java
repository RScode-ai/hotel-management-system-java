package com.hotel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private int guestId;
    private int roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private BookingStatus bookingStatus;
    private BigDecimal totalAmount;

    public Booking() {}

    public Booking(int bookingId, int guestId, int roomId, LocalDate checkInDate, LocalDate checkOutDate,
                    int numberOfGuests, BookingStatus bookingStatus, BigDecimal totalAmount) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.bookingStatus = bookingStatus;
        this.totalAmount = totalAmount;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    @Override
    public String toString() {
        return String.format("ID:%-5d | Guest:%-5d | Room:%-5d | %s to %s | Status:%-12s | Amount:%s",
                bookingId, guestId, roomId, checkInDate, checkOutDate, bookingStatus, totalAmount);
    }
}
