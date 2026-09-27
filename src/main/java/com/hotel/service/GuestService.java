package com.hotel.service;

import com.hotel.dao.GuestDAO;
import com.hotel.exception.ValidationException;
import com.hotel.model.Guest;
import com.hotel.util.InputValidator;

import java.util.List;

public class GuestService {

    private final GuestDAO guestDAO = new GuestDAO();

    public void addGuest(Guest guest) {
        InputValidator.requireNonEmpty(guest.getFirstName(), "First name");
        InputValidator.requireNonEmpty(guest.getLastName(), "Last name");
        InputValidator.validatePhone(guest.getPhone());
        InputValidator.validateEmail(guest.getEmail());
        guestDAO.addGuest(guest);
    }

    public List<Guest> getAllGuests() {
        return guestDAO.getAllGuests();
    }

    public List<Guest> searchByName(String name) {
        return guestDAO.searchByName(name);
    }

    public Guest getGuestById(int guestId) {
        Guest guest = guestDAO.getGuestById(guestId);
        if (guest == null) throw new ValidationException("No guest found with ID " + guestId);
        return guest;
    }

    public void updateGuest(Guest guest) {
        InputValidator.validatePhone(guest.getPhone());
        InputValidator.validateEmail(guest.getEmail());
        boolean updated = guestDAO.updateGuest(guest);
        if (!updated) throw new ValidationException("Guest not found. Update failed.");
    }

    public void deleteGuest(int guestId) {
        boolean deleted = guestDAO.deleteGuest(guestId);
        if (!deleted) throw new ValidationException("Guest not found. Delete failed.");
    }
}
