package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrade.interview.challenge.campsitereservation.Utils;
import lombok.SneakyThrows;

@Service
public class BookingService {

  private final BookingRepository bookingRepository;

  private final BookingDateRepository bookingDateRepository;

  public BookingService(BookingRepository bookingRepository, BookingDateRepository bookingDateRepository) {
    this.bookingRepository = bookingRepository;
    this.bookingDateRepository = bookingDateRepository;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
  public Booking add(Booking booking) {
    final List<LocalDate> bookingDates = bookingDatesBetween(booking.getArrivalDate(), booking.getDepartureDate());
    if (bookingDates.isEmpty()) {
      slowTransactionForTest();
      bookingDateRepository.saveAll(booking.bookingDates());
      return bookingRepository.save(booking);
    } else {
      throw new IllegalStateException("Booking dates not available");
    }
  }

  @Transactional
  public Booking update(Booking oldBooking, Booking newBooking) {
    newBooking.setId(oldBooking.getId());
    newBooking.setVersion(oldBooking.getVersion());
    bookingDateRepository.deleteAll(oldBooking.bookingDates());
    slowTransactionForTest();
    return add(newBooking);
  }

  @Transactional(readOnly = true)
  public Optional<Booking> findById(long id) {
    return bookingRepository.findById(id);
  }

  @Transactional
  public void deleteById(long id) {
    findById(id).ifPresent(booking ->
        bookingDateRepository.deleteAll(booking.bookingDates()));
    bookingRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<Booking> findAll() {
    return bookingRepository.findAll(Sort.by("arrivalDate"));
  }

  @Transactional(readOnly = true)
  public List<LocalDate> getAvailabilities(LocalDate start, LocalDate end) {
    final List<LocalDate> availableDates = Utils.datesBetween(start, end);
    final List<LocalDate> reservedDates = bookingDatesBetween(start, end);
    availableDates.removeAll(reservedDates);
    return availableDates;
  }

  // TODO  @VisibleForTesting
  @SneakyThrows
  void slowTransactionForTest() {
    TimeUnit.SECONDS.sleep(1);
  }

  private List<LocalDate> bookingDatesBetween(LocalDate inclusiveStart, LocalDate exclusiveEnd) {
    return bookingDateRepository.findAllDatesBetween(inclusiveStart, exclusiveEnd)
        .stream()
        .map(BookingDate::getDate)
        .collect(Collectors.toList());
  }
}
