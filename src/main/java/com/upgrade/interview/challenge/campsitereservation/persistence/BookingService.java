package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  private final BookingRepository bookingRepository;

  public BookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

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
}
