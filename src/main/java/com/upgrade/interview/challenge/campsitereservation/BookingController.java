package com.upgrade.interview.challenge.campsitereservation;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    return bookingRepository.findAll();
  }

  @GetMapping(path = "/bookings/{id}")
  public Booking getBooking(@PathVariable long id) {
    return bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("booking " + id + " not found"));
  }

  @PostMapping(path = "/bookings")
  public Booking addBooking(@Valid @RequestBody BookingInput bookingInput) {
    log.info("Add booking {}", bookingInput);
    return bookingRepository.save(Booking.create(bookingInput));
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
