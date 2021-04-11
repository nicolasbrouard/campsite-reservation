package com.upgrade.interview.challenge.campsitereservation.rest;

import java.util.Comparator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.interview.challenge.campsitereservation.persistence.Booking;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class BookingController {

  private final BookingRepository bookingRepository;

  public BookingController(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @GetMapping(path = "/booking/list")
  public Flux<Booking> getBookingListFlux() {
    return Flux.fromIterable(bookingRepository.findAll());
  }

  @GetMapping(path = "/bookings")
  public List<Booking> getBookingList() {
    final List<Booking> bookings = bookingRepository.findAll();
    bookings.sort(Comparator.comparing(Booking::getArrivalDate));
    return bookings;
  }

  @GetMapping(path = "/bookings/{id}")
  public Booking getBooking(@PathVariable long id) {
    return bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("booking " + id + " not found"));
  }

  @PostMapping(path = "/bookings")
  public ResponseEntity<Booking> addBooking(@Valid @RequestBody BookingInput bookingInput) {
    log.info("Add booking {}", bookingInput);
    final long c1 = bookingRepository.countByArrivalDateBetween(bookingInput.getArrivalDate(), bookingInput.getDepartureDate().minusDays(1));
    final long c2 = bookingRepository.countByDepartureDateBetween(bookingInput.getArrivalDate().plusDays(1), bookingInput.getDepartureDate());
    if (c1 + c2 > 0) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
      // TODO "Booking dates not available"
    }
    return ResponseEntity.ok(bookingRepository.save(Booking.create(bookingInput)));
  }

  @PutMapping(path = "/bookings/{id}")
  public Booking updateBooking(@PathVariable long id, @Valid @RequestBody BookingInput bookingInput) {
    log.info("Update booking {} with {}", id, bookingInput);
    final Booking booking = bookingRepository.findById(id)
        .map(b -> {
          b.updateWith(bookingInput);
          return b;
        })
        .orElseGet(() -> {
          final Booking b = Booking.create(bookingInput);
          b.setId(id);
          return b;
        });
    return bookingRepository.save(booking);
  }

  @DeleteMapping(path = "/bookings/{id}")
  public void deleteBooking(@PathVariable long id) {
    log.info("Delete booking {}", id);
    bookingRepository.deleteById(id);
  }
}
