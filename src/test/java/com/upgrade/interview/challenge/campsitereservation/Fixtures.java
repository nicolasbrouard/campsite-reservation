package com.upgrade.interview.challenge.campsitereservation;

import java.time.LocalDate;

import com.upgrade.interview.challenge.campsitereservation.persistence.Booking;
import com.upgrade.interview.challenge.campsitereservation.rest.BookingInput;

public class Fixtures {
  public static Booking createValidBooking() {
    return Booking.builder()
        .id(1)
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(4))
        .build();
  }

  public static BookingInput createValidBookingInput() {
    return BookingInput.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(5))
        .build();
  }

  public static BookingInput createTooEarlyBookingInput() {
    return BookingInput.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now().plusDays(2))
        .build();
  }

  public static BookingInput createTooLateBookingInput() {
    return BookingInput.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(32))
        .departureDate(LocalDate.now().plusDays(33))
        .build();
  }

  public static BookingInput createTooLongBookingInput() {
    return BookingInput.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(6))
        .build();
  }

  public static BookingInput createTooShortBookingInput() {
    return BookingInput.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(2))
        .build();
  }
}
