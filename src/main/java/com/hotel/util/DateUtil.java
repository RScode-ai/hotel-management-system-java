package com.hotel.util;

import com.hotel.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    public static String format(LocalDate date) {
        return date.format(FORMATTER);
    }
}
