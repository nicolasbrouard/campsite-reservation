package com.upgrade.interview.challenge.campsitereservation.rest;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.upgrade.interview.challenge.campsitereservation.validation.BookingConstraint;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@BookingConstraint
public class BookingInput {
  @Email(message = "Email should be valid")
  @NotBlank(message = "Email cannot be blank")
  String email;

  @NotBlank(message = "Fullname cannot be blank")
  String fullname;

  @Future(message = "arrivalDate should be in the future")
  @NotNull(message = "arrivalDate cannot be null")
  LocalDate arrivalDate;

  @Future(message = "departureDate should be in the future")
  @NotNull(message = "departureDate cannot be null")
  LocalDate departureDate;
}
