package com.hotel.model;

public class Guest {
    private int guestId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String idProofType;
    private String idProofNumber;

    public Guest() {}

    public Guest(int guestId, String firstName, String lastName, String phone, String email,
                 String address, String idProofType, String idProofNumber) {
        this.guestId = guestId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.idProofType = idProofType;
        this.idProofNumber = idProofNumber;
    }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIdProofType() { return idProofType; }
    public void setIdProofType(String idProofType) { this.idProofType = idProofType; }
    public String getIdProofNumber() { return idProofNumber; }
    public void setIdProofNumber(String idProofNumber) { this.idProofNumber = idProofNumber; }

    public String getFullName() { return firstName + " " + lastName; }

    @Override
    public String toString() {
        return String.format("ID:%-5d | %-20s | Phone:%-12s | Email:%s",
                guestId, getFullName(), phone, email);
    }
}
