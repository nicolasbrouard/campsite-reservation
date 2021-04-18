package com.upgrade.interview.challenge.campsitereservation.rest;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.interview.challenge.campsitereservation.exception.AlreadyBookedException;
import com.upgrade.interview.challenge.campsitereservation.exception.BadRequestException;
import com.upgrade.interview.challenge.campsitereservation.exception.BookingNotFoundException;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingEntity;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@OpenAPIDefinition(
    externalDocs = @ExternalDocumentation(description = "https://github.com/nicolasbrouard/campsite-reservation"),
    info = @Info(
        title = "Campsite reservation",
        description = "Back-end Tech Challenge: Campsite reservation REST API service",
        contact = @Contact(email = "nicolas.brouard@gmail.com", name = "Nicolas Brouard")))
@Tag(name = "Campsite reservation REST api")
public class BookingController {

  @SuppressWarnings("java:S1075")
  static final String BASE_PATH = "/bookings";

  @SuppressWarnings("java:S1075")
  static final String BASE_AVAILABLE_PATH = "/availabilities";

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @Operation(summary = "Get the list of all reservations.")
  @GetMapping(path = BASE_PATH)
  public Stream<Booking> getBookingList() {
    return bookingService.findAll()
        .stream()
        .map(Booking::createFrom);
  }

  @Operation(summary = "Get the information of the reservation with the given id.")
  @GetMapping(path = BASE_PATH + "/{id}")
  public Booking getBooking(@PathVariable long id) {
    return bookingService.findById(id)
        .map(Booking::createFrom)
        .orElseThrow(() -> new BookingNotFoundException(id));
  }

  @Operation(summary = "Get information of the availability of the campsite for a given date range with the default being 1 month since today.")
  @GetMapping(path = BASE_AVAILABLE_PATH)
  public List<LocalDate> getAvailabilitiesBetween(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  @RequestParam(required = false)
                                                  @Parameter(name = "Start date",
                                                      description = "Start date (included), default: today")
                                                      LocalDate start,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  @RequestParam(required = false)
                                                  @Parameter(name = "End date",
                                                      description = "End date (excluded), default: start date + 1 month")
                                                      LocalDate end) {
    if (start == null) {
      start = LocalDate.now();
    }
    if (end == null) {
      end = start.plusMonths(1);
    }
    if (start.isAfter(end)) {
      throw new BadRequestException(
          MessageFormat.format("Start date {0} is after end date {1}", start, end));
    }
    log.info("Get availabilities between {} and {}", start, end);
    return bookingService.getAvailabilities(start, end);
  }

  @Operation(summary = "Reserve the campsite.")
  @PostMapping(path = BASE_PATH)
  public Booking addBooking(@Valid @RequestBody Booking booking) {
    try {
      log.info("Add booking {}", booking);
      return Booking.createFrom(bookingService.add(BookingEntity.createFrom(booking)));
    } catch (DataAccessException e) {
      throw new AlreadyBookedException("Dates are not available");
    }
  }

  @Operation(summary = "Update the reservation with the given id.")
  @PutMapping(path = BASE_PATH + "/{id}")
  public Booking updateBooking(@PathVariable long id, @Valid @RequestBody Booking booking) {
    log.info("Update booking {} with {}", id, booking);
    final var oldBookingEntity = bookingService.findById(id)
        .orElseThrow(() -> new BookingNotFoundException(id));
    final var newBookingEntity = BookingEntity.createFrom(booking);
    return Booking.createFrom(bookingService.update(oldBookingEntity, newBookingEntity));
  }

  @Operation(summary = "Cancel the reservation with the given id.")
  @DeleteMapping(path = BASE_PATH + "/{id}")
  public void deleteBooking(@PathVariable long id) {
    log.info("Delete booking {}", id);
    try {
      bookingService.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new BookingNotFoundException(id);
    }
  }
}
