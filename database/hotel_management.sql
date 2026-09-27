-- Hotel Management System Database Script
DROP DATABASE IF EXISTS hotel_management;
CREATE DATABASE hotel_management;
USE hotel_management;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','RECEPTIONIST'))
);

CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type VARCHAR(20) NOT NULL CHECK (room_type IN ('SINGLE','DOUBLE','DELUXE','SUITE')),
    price_per_night DECIMAL(10,2) NOT NULL CHECK (price_per_night > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','OCCUPIED','MAINTENANCE'))
);

CREATE TABLE guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200),
    id_proof_type VARCHAR(30),
    id_proof_number VARCHAR(50)
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL DEFAULT 1,
    booking_status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED'
        CHECK (booking_status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED')),
    total_amount DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_booking_guest FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL CHECK (payment_method IN ('CASH','CARD','UPI')),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN ('PAID','PENDING','REFUNDED')),
    payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- Sample Users (password stored as plain text for this learning project)
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('reception', 'reception123', 'RECEPTIONIST');

-- Sample Rooms
INSERT INTO rooms (room_number, room_type, price_per_night, status) VALUES
('101','SINGLE',1500.00,'AVAILABLE'),
('102','SINGLE',1500.00,'AVAILABLE'),
('103','DOUBLE',2500.00,'AVAILABLE'),
('104','DOUBLE',2500.00,'AVAILABLE'),
('105','DELUXE',3500.00,'AVAILABLE'),
('106','DELUXE',3500.00,'AVAILABLE'),
('107','SUITE',5000.00,'AVAILABLE'),
('108','SUITE',5000.00,'AVAILABLE'),
('109','DOUBLE',2500.00,'MAINTENANCE'),
('110','SINGLE',1500.00,'AVAILABLE');

-- Sample Guests
INSERT INTO guests (first_name, last_name, phone, email, address, id_proof_type, id_proof_number) VALUES
('Rohit','Sharma','9876543210','rohit.sharma@example.com','Jaipur, Rajasthan','Aadhaar','1234-5678-9012'),
('Anita','Verma','9876543211','anita.verma@example.com','Delhi','PAN','ABCDE1234F'),
('Sanjay','Patel','9876543212','sanjay.patel@example.com','Ahmedabad','Aadhaar','2345-6789-0123'),
('Priya','Nair','9876543213','priya.nair@example.com','Kochi','Passport','P1234567'),
('Amit','Singh','9876543214','amit.singh@example.com','Lucknow','Aadhaar','3456-7890-1234');
