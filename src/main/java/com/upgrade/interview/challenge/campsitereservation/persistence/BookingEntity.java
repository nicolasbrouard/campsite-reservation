package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import com.upgrade.interview.challenge.campsitereservation.Utils;
import com.upgrade.interview.challenge.campsitereservation.rest.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Booking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

  @Id
  @GeneratedValue
  private long id;

  @Version
  @GeneratedValue
  private long version;

  private String email;

  private String fullname;

  private LocalDate arrivalDate;

  private LocalDate departureDate;

  /**
   * Creates a BookingEntity object from Booking object.
   */
  public static BookingEntity createFrom(Booking booking) {
    return BookingEntity.builder()
        .email(booking.getEmail())
        .fullname(booking.getFullname())
        .arrivalDate(booking.getArrivalDate())
        .departureDate(booking.getDepartureDate())
        .build();
  }

  public List<BookingDate> bookingDates() {
    return Utils.datesBetween(arrivalDate, departureDate)
        .stream()
        .map(localDate -> BookingDate.builder().date(localDate).build())
        .collect(Collectors.toList());
  }
}
