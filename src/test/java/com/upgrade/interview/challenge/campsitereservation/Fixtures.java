package com.upgrade.interview.challenge.campsitereservation;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.upgrade.interview.challenge.campsitereservation.persistence.BookingDate;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingEntity;
import com.upgrade.interview.challenge.campsitereservation.rest.Booking;

public class Fixtures {
  public static BookingEntity createValidBookingEntity(LocalDate arrivalDate, int numberOfDays) {
    return BookingEntity.builder()
        .id(1)
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(arrivalDate)
        .departureDate(arrivalDate.plusDays(numberOfDays))
        .version(0)
        .build();
  }

  public static BookingEntity createValidBookingEntity() {
    return createValidBookingEntity(LocalDate.now().plusDays(2), 2);
  }

  public static Booking createBooking(LocalDate arrivalDate, int numberOfDays) {
    return Booking.builder()
        .email("name@email.com")
        .fullname("name")
        .arrivalDate(LocalDate.now().plusDays(2))
        .departureDate(LocalDate.now().plusDays(5))
        .build();
  }

  public static Booking createValidBooking() {
    return createBooking(LocalDate.now().plusDays(2), 3);
  }

  public static Booking createTooEarlyBooking() {
    return createBooking(LocalDate.now(), 2);
  }

  public static Booking createTooLateBooking() {
    return createBooking(LocalDate.now().plusDays(32), 1);
  }

  public static Booking createTooLongBooking() {
    return createBooking(LocalDate.now().plusDays(2), 4);
  }

  public static Booking createTooShortBooking() {
    return createBooking(LocalDate.now().plusDays(2), 0);
  }

  public static Stream<BookingDate> bookingDates(String startInclusive, int numberOfDays) {
    final LocalDate start = LocalDate.parse(startInclusive);
    final LocalDate end = start.plusDays(numberOfDays);
    return start.datesUntil(end)
        .map(date -> BookingDate.builder().date(date).build());
  }
}
