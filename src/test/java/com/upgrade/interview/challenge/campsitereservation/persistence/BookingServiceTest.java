package com.upgrade.interview.challenge.campsitereservation.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.upgrade.interview.challenge.campsitereservation.Fixtures;
import com.upgrade.interview.challenge.campsitereservation.Utils;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class BookingServiceTest {

  @Autowired
  private BookingService bookingService;

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private BookingDateRepository bookingDateRepository;

  @BeforeEach
  void setUp() {
    bookingRepository.deleteAll();
    bookingDateRepository.deleteAll();
  }

  private BookingEntity createAndAddBookingEntity() {
    final BookingEntity validBookingEntity = Fixtures.createBookingEntity();
    return bookingService.add(validBookingEntity);
  }

  private BookingEntity createAndAddAnotherBookingEntity() {
    final BookingEntity validBookingEntity = Fixtures.createAnotherBookingEntity();
    return bookingService.add(validBookingEntity);
  }

  @Test
  void add_success() {
    final BookingEntity bookingEntity = Fixtures.createBookingEntity();

    final BookingEntity addedBookingEntity = bookingService.add(bookingEntity);

    assertThat(bookingRepository.findById(addedBookingEntity.getId()))
        .get().usingRecursiveComparison().ignoringFields("id", "version").isEqualTo(bookingEntity);
    assertThat(bookingRepository.findById(addedBookingEntity.getId())).get().isEqualTo(addedBookingEntity);
    assertThat(bookingDateRepository.findAll()).containsExactlyElementsOf(bookingEntity.bookingDates());
  }

  @Test
  void update() {
    final BookingEntity oldBookingEntity = Fixtures.createBookingEntity();
    final BookingEntity newBookingEntity = Fixtures.createAnotherBookingEntity();

    final BookingEntity updatedBookingEntity = bookingService.update(oldBookingEntity, newBookingEntity);

    assertThat(bookingRepository.findById(updatedBookingEntity.getId()))
        .get().usingRecursiveComparison().ignoringFields("id", "version").isEqualTo(newBookingEntity);
    assertThat(bookingRepository.findById(updatedBookingEntity.getId())).get().isEqualTo(updatedBookingEntity);
    assertThat(bookingDateRepository.findAll()).containsExactlyElementsOf(newBookingEntity.bookingDates());
  }

  @Test
  void findById_absent() {
    assertThat(bookingService.findById(0)).isNotPresent();
  }

  @Test
  void findById_success() {
    final BookingEntity bookingEntity = createAndAddBookingEntity();

    final Optional<BookingEntity> optionalBookingEntity = bookingService.findById(bookingEntity.getId());

    assertThat(optionalBookingEntity).get().isEqualTo(bookingEntity);
  }

  @Test
  void deleteById_failure() {
    assertThatThrownBy(() -> bookingService.deleteById(0))
        .isInstanceOf(EmptyResultDataAccessException.class);
  }

  @Test
  void deleteById_success() {
    final BookingEntity bookingEntity = createAndAddBookingEntity();

    bookingService.deleteById(bookingEntity.getId());

    assertThat(bookingRepository.findAll()).isEmpty();
    assertThat(bookingDateRepository.findAll()).isEmpty();
  }

  @Test
  void findAll_empty() {
    assertThat(bookingService.findAll()).isEmpty();
  }

  @Test
  void findAll_1() {
    final BookingEntity bookingEntity = createAndAddBookingEntity();

    final List<BookingEntity> bookingEntities = bookingService.findAll();
    assertThat(bookingEntities).containsExactly(bookingEntity);
  }

  @Test
  void findAll_2() {
    final BookingEntity bookingEntity1 = createAndAddBookingEntity();
    final BookingEntity bookingEntity2 = createAndAddAnotherBookingEntity();

    final List<BookingEntity> bookingEntities = bookingService.findAll();

    assertThat(bookingEntities).containsExactly(bookingEntity1, bookingEntity2);
  }

  @Test
  void getAvailabilities_0booking() {
    final LocalDate startInclusive = LocalDate.now();
    final LocalDate endExclusive = startInclusive.plusDays(10);

    final List<LocalDate> availabilities = bookingService.getAvailabilities(startInclusive, endExclusive);

    final List<LocalDate> expectedAvailabilities = Utils.datesBetween(startInclusive, endExclusive);
    assertThat(availabilities).containsExactlyElementsOf(expectedAvailabilities);
  }

  @Test
  void getAvailabilities_1booking() {
    final BookingEntity bookingEntity = createAndAddBookingEntity();
    // Get the availabilities between 5 days before and 5 days after the booking
    final LocalDate startInclusive = bookingEntity.getArrivalDate().minusDays(5);
    final LocalDate endExclusive = bookingEntity.getDepartureDate().plusDays(5);

    final List<LocalDate> availabilities = bookingService.getAvailabilities(startInclusive, endExclusive);

    final List<LocalDate> expectedAvailabilities = Utils.datesBetween(startInclusive, bookingEntity.getArrivalDate());
    expectedAvailabilities.addAll(Utils.datesBetween(bookingEntity.getDepartureDate(), endExclusive));
    assertThat(availabilities).containsExactlyElementsOf(expectedAvailabilities);
  }

  @Test
  void getAvailabilities_2bookings() {
    final BookingEntity bookingEntity1 = createAndAddBookingEntity();
    final BookingEntity bookingEntity2 = createAndAddAnotherBookingEntity();
    // Get the availabilities between the start of the booking1 to the end of the booking2
    final LocalDate startInclusive = bookingEntity1.getArrivalDate();
    final LocalDate endExclusive = bookingEntity2.getDepartureDate();

    final List<LocalDate> availabilities = bookingService.getAvailabilities(startInclusive, endExclusive);

    final List<LocalDate> expectedAvailabilities = Utils.datesBetween(
        bookingEntity1.getDepartureDate(),
        bookingEntity2.getArrivalDate());
    assertThat(availabilities).containsExactlyElementsOf(expectedAvailabilities);
  }
}