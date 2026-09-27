package com.hotel.service;

import com.hotel.dao.UserDAO;
import com.hotel.model.User;

public class AuthenticationService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null; // invalid username
        }
        if (!user.getPassword().equals(password)) {
            return null; // invalid password
        }
        return user;
    }
}
