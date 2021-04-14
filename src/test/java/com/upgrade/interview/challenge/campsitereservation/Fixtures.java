package com.upgrade.interview.challenge.campsitereservation;

import java.time.LocalDate;

import com.upgrade.interview.challenge.campsitereservation.persistence.BookingEntity;
import com.upgrade.interview.challenge.campsitereservation.rest.Booking;

public class Fixtures {
  public static BookingEntity createValidBooking() {
    return BookingEntity.builder()
        .id(1)
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(4))
        .build();
  }

  public static Booking createValidBookingInput() {
    return Booking.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(5))
        .build();
  }

  public static Booking createTooEarlyBookingInput() {
    return Booking.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now().plusDays(2))
        .build();
  }

  public static Booking createTooLateBookingInput() {
    return Booking.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(32))
        .departureDate(LocalDate.now().plusDays(33))
        .build();
  }

  public static Booking createTooLongBookingInput() {
    return Booking.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(6))
        .build();
  }

  public static Booking createTooShortBookingInput() {
    return Booking.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(2))
        .build();
  }

  public static BookingEntity createValidBooking(LocalDate arrivalDate, int numberOfDays) {
    return BookingEntity.builder()
        .id(1)
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(arrivalDate)
        .departureDate(arrivalDate.plusDays(numberOfDays))
        .build();
  }
}
