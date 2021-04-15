package com.upgrade.interview.challenge.campsitereservation.exception;

public class BookingNotFoundException  extends RuntimeException {
  public BookingNotFoundException(long id) {
    super("Could not find booking with id " + id);
  }
}
