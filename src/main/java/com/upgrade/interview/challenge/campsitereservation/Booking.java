package com.upgrade.interview.challenge.campsitereservation;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
  public static Booking create(BookingInput bookingInput) {
    return Booking.builder()
        .email(bookingInput.getEmail())
        .fullname(bookingInput.getFullname())
        .arrivalDate(bookingInput.getArrivalDate())
        .departureDate(bookingInput.getDepartureDate())
        .build();
  }

  @Id
  @GeneratedValue
  private long id;

  private String email;

  private String fullname;

//  @Temporal(TemporalType.DATE)
  private LocalDate arrivalDate;

//  @Temporal(TemporalType.DATE)
  private LocalDate departureDate;

  public void updateWith(BookingInput bookingInput) {
    setFullname(bookingInput.getFullname());
    setArrivalDate(bookingInput.getArrivalDate());
    setDepartureDate(bookingInput.getDepartureDate());
  }
}
