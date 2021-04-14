package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  private final BookingRepository bookingRepository;

  public BookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  // TODO not good if we have multiple instances of this service
  public synchronized <S extends Booking> S add(S entity) {
    final long c1 = bookingRepository.countByArrivalDateBetween(entity.getArrivalDate(), entity.getDepartureDate().minusDays(1));
    final long c2 = bookingRepository.countByDepartureDateBetween(entity.getArrivalDate().plusDays(1), entity.getDepartureDate());
    if (c1 + c2 > 0) {
      throw new IllegalStateException("Booking dates not available");
    } else {
      return bookingRepository.save(entity);
    }
  }

  public Optional<Booking> findById(long id) {
    return bookingRepository.findById(id);
  }

  public void deleteById(long id) {
    bookingRepository.deleteById(id);
  }

  public List<Booking> findAll() {
    return bookingRepository.findAll(Sort.by("arrivalDate"));
  }

  // getAvailabilities will return the list of days displayed by 'o' below:
  //
  // bookings:
  // days:     ----oooooooooooooo--------
  //               |             |
  //              start         end
  //
  // bookings:           xxx
  // days:     ----oooooo--oooooo--------
  //               |             |
  //              start         end
  //
  // bookings:    xxx  xx       xxx
  // days:     -----ooo-oooooooo----------
  //               |            |
  //              start        end
  @Transactional(readOnly = true)
  public List<LocalDate> getAvailabilities(LocalDate start, LocalDate end) {
    // TODO is there a way not to do 2 select?
    final List<Booking> bookings = Stream.concat(
        bookingRepository.findAllByArrivalDateBetween(start, end.minusDays(1)),
        bookingRepository.findAllByDepartureDateBetween(start.plusDays(1), end))
        .sorted(Comparator.comparing(Booking::getArrivalDate))
        .distinct()
        .collect(Collectors.toList());

    if (bookings.isEmpty()) {
      return datesBetween(start, end);
    }

    // TODO use stream
    final List<LocalDate> availabilities = new ArrayList<>();
    final Iterator<Booking> iterator = bookings.iterator();

    final Booking firstBooking = iterator.next();
    availabilities.addAll(datesBetween(start, firstBooking.getArrivalDate()));

    Booking previous = firstBooking;
    while (iterator.hasNext()) {
      final Booking booking = iterator.next();
      availabilities.addAll(datesBetween(previous.getDepartureDate(), booking.getArrivalDate()));
      previous = booking;
    }
    availabilities.addAll(datesBetween(previous.getDepartureDate(), end));
    return availabilities;
  }

  /**
   * Returns a list of date between inclusiveStart and exclusiveEnd or an empty list.
   * Example: datesBetween('2021-04-22', '2021-04-25') returns:
   * - '2021-04-22'
   * - '2021-04-23'
   * - '2021-04-24'
   */
  private List<LocalDate> datesBetween(LocalDate inclusiveStart, LocalDate exclusiveEnd) {
    if (inclusiveStart.isAfter(exclusiveEnd)) {
      return List.of();
    }
    final Stream<LocalDate> localDateStream = inclusiveStart.datesUntil(exclusiveEnd);
    return localDateStream.collect(Collectors.toList());
  }
}
