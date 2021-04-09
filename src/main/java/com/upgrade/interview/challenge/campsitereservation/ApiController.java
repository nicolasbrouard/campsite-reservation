package com.upgrade.interview.challenge.campsitereservation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class ApiController {

  private final BookingRepository bookingRepository;

  public ApiController(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @GetMapping(path="/bookings")
  public List<Booking> getBookingList() {
    return bookingRepository.findAll();
  }

  @GetMapping(path="/booking/list")
  public Flux<Booking> getBookingListFlux() {
    return Flux.fromIterable(bookingRepository.findAll());
  }
}
