package com.upgrade.interview.challenge.campsitereservation.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.interview.challenge.campsitereservation.persistence.BookingEntity;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BookingController {

  @SuppressWarnings("java:S1075")
  static final String BASE_PATH = "/bookings";

  @SuppressWarnings("java:S1075")
  static final String BASE_AVAILABLE_PATH = "/availabilities";

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping(path = BASE_PATH)
  public Stream<Booking> getBookingList() {
    return bookingService.findAll()
        .stream()
        .map(Booking::createFrom);
  }

  @GetMapping(path = BASE_PATH + "/{id}")
  public ResponseEntity<Booking> getBooking(@PathVariable long id) {
    return ResponseEntity.of(bookingService.findById(id).map(Booking::createFrom));
  }

  @GetMapping(path = BASE_AVAILABLE_PATH)
  public List<LocalDate> getAvailabilitiesBetween(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  @RequestParam(required = false) LocalDate start,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  @RequestParam(required = false) LocalDate end) {
    if (start == null) {
      start = LocalDate.now();
    }
    if (end == null) {
      end = start.plusMonths(1);
    }
    if (start.isAfter(end)) {
      throw new IllegalArgumentException("start after end"); // TODO return 400
    }
    log.info("get availabilities between {} and {}", start, end);
    return bookingService.getAvailabilities(start, end);
  }

  @PostMapping(path = BASE_PATH)
  public Booking addBooking(@Valid @RequestBody Booking booking) {
    log.info("Add booking {}", booking);
    return Booking.createFrom(bookingService.add(BookingEntity.createFrom(booking)));
  }

  @PutMapping(path = BASE_PATH + "/{id}")
  public Booking updateBooking(@PathVariable long id, @Valid @RequestBody Booking booking) {
    log.info("Update booking {} with {}", id, booking);
    final BookingEntity oldBookingEntity = bookingService.findById(id).orElseThrow(NoSuchElementException::new);// TODO
    final BookingEntity newBookingEntity = BookingEntity.createFrom(booking);
    return Booking.createFrom(bookingService.update(oldBookingEntity, newBookingEntity));
  }

  @DeleteMapping(path = BASE_PATH + "/{id}")
  public void deleteBooking(@PathVariable long id) {
    log.info("Delete booking {}", id);
    bookingService.deleteById(id);
    // TODO catch EmptyResultDataAccessException
  }
}
