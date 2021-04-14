package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import com.upgrade.interview.challenge.campsitereservation.Utils;
import com.upgrade.interview.challenge.campsitereservation.rest.BookingInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Booking")
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

  @Version
  @GeneratedValue
  private long version;

  private String email;

  private String fullname;

//  @Temporal(TemporalType.DATE)
  private LocalDate arrivalDate;

//  @Temporal(TemporalType.DATE)
  private LocalDate departureDate;

  public List<BookingDate> bookingDates() {
    return Utils.datesBetween(arrivalDate, departureDate)
        .stream()
        .map(localDate -> BookingDate.builder().date(localDate).build())
        .collect(Collectors.toList());
  }
}
