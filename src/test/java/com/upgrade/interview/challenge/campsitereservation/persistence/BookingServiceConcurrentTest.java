package com.upgrade.interview.challenge.campsitereservation.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.upgrade.interview.challenge.campsitereservation.Fixtures;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class BookingServiceConcurrentTest {

  private static final int DELAY = 1000;

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

  private void insertDelayInsideLockingSection() {
    doAnswer(invocation -> {
      try {
        log.info("Inserting delay of 100 ms inside the locking section");
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

  @Test
  void addWithConcurrency() throws Exception {
    final var bookingEntity1 = Fixtures.createBookingEntity();
    final var bookingEntity2 = createOverlappedBookingEntity(bookingEntity1);
    insertDelayInsideLockingSection();
    final var executor = Executors.newFixedThreadPool(2);

    executor.execute(() -> bookingService.add(bookingEntity1));
    TimeUnit.MILLISECONDS.sleep(DELAY / 2);
    executor.execute(() -> bookingService.add(bookingEntity2));
    executor.shutdown();

    assertThat(executor.awaitTermination(DELAY * 2, TimeUnit.MILLISECONDS)).isTrue();
    assertThat(bookingRepository.findAll()).containsExactly(bookingEntity1);
    assertThat(bookingDateRepository.findAll()).containsExactlyElementsOf(bookingEntity1.bookingDates());
  }
}