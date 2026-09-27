package com.hotel.util;

import com.hotel.exception.ValidationException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return; // optional field
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format.");
        }
    }

    public static void validatePhone(String phone) {
        requireNonEmpty(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Invalid phone number. Enter a valid 10-digit number.");
        }
    }

    public static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid " + fieldName + ". Please enter a whole number.");
        }
    }

    public static BigDecimal parseDecimal(String value, String fieldName) {
        try {
            BigDecimal result = new BigDecimal(value.trim());
            if (result.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException(fieldName + " cannot be negative.");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid " + fieldName + ". Please enter a valid number.");
        }
    }
}
