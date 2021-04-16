package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.upgrade.interview.challenge.campsitereservation.Utils;
import com.upgrade.interview.challenge.campsitereservation.exception.AlreadyBookedException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingService {

  private final BookingRepository bookingRepository;

  private final BookingDateRepository bookingDateRepository;

  public BookingService(BookingRepository bookingRepository, BookingDateRepository bookingDateRepository) {
    this.bookingRepository = bookingRepository;
    this.bookingDateRepository = bookingDateRepository;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public BookingEntity add(BookingEntity bookingEntity) {
    log.info("Adding {}", bookingEntity);
    // Could throw CannotAcquireLockException
    final var bookingDatesBetween = bookingDateRepository.findAllDatesBetween(bookingEntity.getArrivalDate(), bookingEntity.getDepartureDate());
    final var bookingDates = convert(bookingDatesBetween);
    if (bookingDates.isEmpty()) {
      log.error("Saving {}", bookingEntity);
      insertArtificialDelayForTestsOnly();
      // Could throw DataIntegrityViolationException (primary key constraint)
      bookingDateRepository.saveAll(bookingEntity.bookingDates());
      // Could fail because of version update ObjectOptimisticLockingFailureException
      final var addedBookingEntity = bookingRepository.save(bookingEntity);
      log.info("Added {}", addedBookingEntity);
      return addedBookingEntity;
    } else {
      throw new AlreadyBookedException("Dates " + bookingDates + " are not available");
    }
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public BookingEntity update(BookingEntity oldBookingEntity, BookingEntity newBookingEntity) {
    log.info("Updating {} with {}", oldBookingEntity, newBookingEntity);
    newBookingEntity.setId(oldBookingEntity.getId());
    newBookingEntity.setVersion(oldBookingEntity.getVersion());
    bookingDateRepository.deleteAll(oldBookingEntity.bookingDates());
    return add(newBookingEntity);
  }

  @Transactional(readOnly = true)
  public Optional<BookingEntity> findById(long id) {
    log.info("Find booking with id {}", id);
    return bookingRepository.findById(id);
  }

  @Transactional
  public void deleteById(long id) {
    log.info("Deleting booking with id {}", id);
    findById(id).ifPresent(booking ->
        bookingDateRepository.deleteAll(booking.bookingDates()));
    bookingRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<BookingEntity> findAll() {
    log.info("Find all booking");
    return bookingRepository.findAll(Sort.by("arrivalDate"));
  }

  @Transactional(readOnly = true)
  public List<LocalDate> getAvailabilities(LocalDate startInclusive, LocalDate endExclusive) {
    log.info("Get availabilities between {} and {}", startInclusive, endExclusive);
    final var availableDates = Utils.datesBetween(startInclusive, endExclusive);
    final var reservedDates = convert(
        bookingDateRepository.fastFindAllDatesBetween(startInclusive, endExclusive));
    availableDates.removeAll(reservedDates);
    return availableDates;
  }

  @VisibleForTesting
  void insertArtificialDelayForTestsOnly() {
    // This method is intentionally empty.
    // It can be use in unit test to insert an artificial delay for concurrency testing.
  }

  private List<LocalDate> convert(Stream<BookingDate> bookingDateStream) {
    return bookingDateStream
        .map(BookingDate::getDate)
        .collect(Collectors.toList());
  }
}
