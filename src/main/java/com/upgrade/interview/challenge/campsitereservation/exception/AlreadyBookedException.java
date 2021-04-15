package com.upgrade.interview.challenge.campsitereservation.exception;

public class AlreadyBookedException extends RuntimeException {
  public AlreadyBookedException(String message) {
    super(message);
  }
}
