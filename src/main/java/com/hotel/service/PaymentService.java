package com.hotel.service;

import com.hotel.dao.PaymentDAO;
import com.hotel.exception.ValidationException;
import com.hotel.model.Booking;
import com.hotel.model.Payment;
import com.hotel.model.PaymentMethod;
import com.hotel.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final BookingService bookingService = new BookingService();

    public Payment makePayment(int bookingId, PaymentMethod method) {
        Booking booking = bookingService.getBookingById(bookingId);
        BigDecimal amount = booking.getTotalAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid payment amount.");
        }
        Payment payment = new Payment(0, bookingId, amount, method, PaymentStatus.PAID, LocalDateTime.now());
        paymentDAO.createPayment(payment);
        return payment;
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) {
        return paymentDAO.getPaymentsByBookingId(bookingId);
    }

    public Payment getPaymentById(int paymentId) {
        Payment payment = paymentDAO.getPaymentById(paymentId);
        if (payment == null) throw new ValidationException("No payment found with ID " + paymentId);
        return payment;
    }
}
