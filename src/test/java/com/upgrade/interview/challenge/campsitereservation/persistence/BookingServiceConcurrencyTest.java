package com.upgrade.interview.challenge.campsitereservation.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.TransientDataAccessException;

import com.upgrade.interview.challenge.campsitereservation.Fixtures;
import lombok.extern.slf4j.Slf4j;

/**
 * Concurrency tests for the class {@link BookingService}.
 * <p>This test class uses the h2 in-memory database.</p>
 * <p>A delay is artificially added inside the method {@link BookingService#add(BookingEntity)} to make sure a second
 * call (from another thread) occurs during the execution of the first call. The expected result is the second </p>
 */
@SpringBootTest
@Slf4j
class BookingServiceConcurrencyTest {

  private static final int DELAY = 100;

  @SpyBean
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

  /**
   * Test the creation of 2 entities (without overlapping date) in parallel. Both should be successful.
   */
  @Test
  void addWithConcurrency_no_conflict() throws Exception {
    final var bookingEntity1 = Fixtures.createBookingEntity();
    final var bookingEntity2 = Fixtures.createAnotherBookingEntity();
    insertDelayInsideLockingSection();
    final var executor = Executors.newFixedThreadPool(2);

    // Creation of the first entity (should be successful)
    final var future1 = executor.submit(() -> bookingService.add(bookingEntity1));

    // Creation of the second entity (should be successful)
    final var future2 = executor.submit(() -> bookingService.add(bookingEntity2));

    executor.shutdown();
    assertThat(executor.awaitTermination(DELAY * 4, TimeUnit.MILLISECONDS)).isTrue();
    assertThat(future1.get()).isEqualTo(bookingEntity1);
    assertThat(future2.get()).isEqualTo(bookingEntity2);
    assertThat(bookingRepository.findAll()).containsExactlyInAnyOrder(bookingEntity1, bookingEntity2);
    var expectedBookingDates = bookingEntity1.bookingDates();
    expectedBookingDates.addAll(bookingEntity2.bookingDates());
    assertThat(bookingDateRepository.findAll()).containsExactlyElementsOf(expectedBookingDates);
  }

  /**
   * Test the creation of 2 entities (with overlapping date) in parallel. The second one should fail.
   */
  @Test
  void addWithConcurrency_conflict() throws Exception {
    final var bookingEntity1 = Fixtures.createBookingEntity();
    final var bookingEntity2 = createOverlappedBookingEntity(bookingEntity1);
    insertDelayInsideLockingSection();
    final var executor = Executors.newFixedThreadPool(2);

    // Creation of the first entity (should be successful)
    final var future1 = executor.submit(() -> bookingService.add(bookingEntity1));

    // Wait a bit to make sure the first entity wins.
    TimeUnit.MILLISECONDS.sleep(DELAY / 2);

    // Creation of the second conflicting entity (should fail)
    final var future2 = executor.submit(() -> bookingService.add(bookingEntity2));

    executor.shutdown();
    assertThat(executor.awaitTermination(DELAY * 4, TimeUnit.MILLISECONDS)).isTrue();
    assertThat(future1.get()).isEqualTo(bookingEntity1);
    // NonTransientDataAccessException means a retry of the failed operation would fail.
    assertThatThrownBy(future2::get).hasCauseInstanceOf(NonTransientDataAccessException.class);
    assertThat(bookingRepository.findAll()).containsExactly(bookingEntity1);
    assertThat(bookingDateRepository.findAll()).containsExactlyElementsOf(bookingEntity1.bookingDates());
  }

  /**
   * Test the concurrent update of 1 entity. The second one should fail.
   */
  @Test
  void updateWithConcurrency() throws Exception {
    // Creation of 1 entity (should be successful)
    final BookingEntity addedBookingEntity = bookingService.add(Fixtures.createBookingEntity());
    assertThat(bookingRepository.findAll()).containsExactly(addedBookingEntity);
    assertThat(bookingDateRepository.findAll()).containsExactlyElementsOf(addedBookingEntity.bookingDates());

    final var bookingEntity1 = createOverlappedBookingEntity(addedBookingEntity);
    final var bookingEntity2 = createOverlappedBookingEntity(bookingEntity1);
    insertDelayInsideLockingSection();
    final var executor = Executors.newFixedThreadPool(2);

    // Update of the first entity (should be successful)
    var future1 = executor.submit(() -> bookingService.update(addedBookingEntity, bookingEntity1));
    TimeUnit.MILLISECONDS.sleep(DELAY / 2);

    // Second update of the same entity (should fail)
    var future2 = executor.submit(() -> bookingService.update(addedBookingEntity, bookingEntity2));
    executor.shutdown();
    assertThat(executor.awaitTermination(DELAY * 4, TimeUnit.MILLISECONDS)).isTrue();

    // The first update will increase the version of the updated entity
    bookingEntity1.setVersion(bookingEntity1.getVersion() + 1);
    assertThat(future1.get()).isEqualTo(bookingEntity1);
    // TransientDataAccessException means a retry of the failed operation would succeed.
    assertThatThrownBy(future2::get).hasCauseInstanceOf(TransientDataAccessException.class);
    assertThat(bookingRepository.findAll()).containsExactly(bookingEntity1);
    assertThat(bookingDateRepository.findAll()).containsExactlyElementsOf(bookingEntity1.bookingDates());
  }

  private void insertDelayInsideLockingSection() {
    doAnswer(invocation -> {
      try {
        log.info("Inserting delay of {} ms inside the locking section", DELAY);
        TimeUnit.MILLISECONDS.sleep(DELAY);
      } catch (InterruptedException ignore) {
        Thread.currentThread().interrupt();
      }
      return null;
    }).when(bookingService).insertArtificialDelayForTestsOnly();
  }

  private BookingEntity createOverlappedBookingEntity(BookingEntity bookingEntity) {
    return BookingEntity.builder()
        .email("someone@email.com")
        .fullname("Someone")
        .arrivalDate(bookingEntity.getArrivalDate().minusDays(1))
        .departureDate(bookingEntity.getDepartureDate())
        .build();
  }
}