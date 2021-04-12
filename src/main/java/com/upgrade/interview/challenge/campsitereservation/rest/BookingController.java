package com.upgrade.interview.challenge.campsitereservation.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.interview.challenge.campsitereservation.persistence.Booking;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BookingController {

  @SuppressWarnings("java:S1075")
  static final String BASE_PATH = "/bookings";

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping(path = BASE_PATH)
  public List<Booking> getBookingList() {
    return bookingService.findAll();
  }

  @GetMapping(path = BASE_PATH + "/{id}")
  public ResponseEntity<Booking> getBooking(@PathVariable long id) {
    return ResponseEntity.of(bookingService.findById(id));
  }

  @PostMapping(path = BASE_PATH)
  public Booking addBooking(@Valid @RequestBody BookingInput bookingInput) {
    log.info("Add booking {}", bookingInput);
    return bookingService.add(Booking.create(bookingInput));
  }

  @PutMapping(path = BASE_PATH + "/{id}")
  public Booking updateBooking(@PathVariable long id, @Valid @RequestBody BookingInput bookingInput) {
    log.info("Update booking {} with {}", id, bookingInput);
    final Booking booking = bookingService.findById(id)
        .map(b -> {
          b.updateWith(bookingInput);
          return b;
        })
        .orElseGet(() -> {
          final Booking b = Booking.create(bookingInput);
          b.setId(id);
          return b;
        });
    return bookingService.add(booking);
  }

  @DeleteMapping(path = BASE_PATH + "/{id}")
  public void deleteBooking(@PathVariable long id) {
    log.info("Delete booking {}", id);
    bookingService.deleteById(id);
  }
}
