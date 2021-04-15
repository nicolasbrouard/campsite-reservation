package com.upgrade.interview.challenge.campsitereservation.exception;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}
