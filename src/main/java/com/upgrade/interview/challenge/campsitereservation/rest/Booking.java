package com.upgrade.interview.challenge.campsitereservation.rest;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.upgrade.interview.challenge.campsitereservation.persistence.BookingEntity;
import com.upgrade.interview.challenge.campsitereservation.validation.BookingConstraint;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@BookingConstraint
public class Booking {

  /**
   * Creates a Booking object from BookingEntity object.
   */
  public static Booking createFrom(BookingEntity bookingEntity) {
    return Booking.builder()
        .email(bookingEntity.getEmail())
        .fullname(bookingEntity.getFullname())
        .arrivalDate(bookingEntity.getArrivalDate())
        .departureDate(bookingEntity.getDepartureDate())
        .id(bookingEntity.getId())
        .build();
  }

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email cannot be blank")
  String email;

  @NotBlank(message = "Fullname cannot be blank")
  String fullname;

  @Future(message = "ArrivalDate should be in the future")
  @NotNull(message = "ArrivalDate is mandatory")
  LocalDate arrivalDate;

  @Future(message = "DepartureDate should be in the future")
  @NotNull(message = "DepartureDate is mandatory")
  LocalDate departureDate;

  long id;
}
