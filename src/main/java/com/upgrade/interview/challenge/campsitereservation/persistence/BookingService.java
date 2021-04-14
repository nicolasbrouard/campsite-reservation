package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
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

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public BookingEntity add(BookingEntity bookingEntity) {
    final List<LocalDate> bookingDates = bookingDatesBetween(bookingEntity.getArrivalDate(), bookingEntity.getDepartureDate());
    if (bookingDates.isEmpty()) {
      slowTransactionForTest();
      bookingDateRepository.saveAll(bookingEntity.bookingDates());
      return bookingRepository.save(bookingEntity);
    } else {
      throw new IllegalStateException("Booking dates not available");
    }
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public BookingEntity update(BookingEntity oldBookingEntity, BookingEntity newBookingEntity) {
    newBookingEntity.setId(oldBookingEntity.getId());
    newBookingEntity.setVersion(oldBookingEntity.getVersion());
    bookingDateRepository.deleteAll(oldBookingEntity.bookingDates());
    slowTransactionForTest();
    return add(newBookingEntity);
  }

  @Transactional(readOnly = true)
  public Optional<BookingEntity> findById(long id) {
    return bookingRepository.findById(id);
  }

  @Transactional
  public void deleteById(long id) {
    findById(id).ifPresent(booking ->
        bookingDateRepository.deleteAll(booking.bookingDates()));
    bookingRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<BookingEntity> findAll() {
    return bookingRepository.findAll(Sort.by("arrivalDate"));
  }

  @Transactional(readOnly = true)
  public List<LocalDate> getAvailabilities(LocalDate start, LocalDate end) {
    final List<LocalDate> availableDates = Utils.datesBetween(start, end);
    final List<LocalDate> reservedDates = bookingDatesBetween(start, end);
    availableDates.removeAll(reservedDates);
    return availableDates;
  }

  @VisibleForTesting // TODO write test
  @SneakyThrows
  void slowTransactionForTest() {
    TimeUnit.SECONDS.sleep(1);
  }

  private List<LocalDate> bookingDatesBetween(LocalDate startInclusive, LocalDate endExclusive) {
    return bookingDateRepository.findAllDatesFastBetween(startInclusive, endExclusive)
        .map(BookingDate::getDate)
        .collect(Collectors.toList());
  }
}
