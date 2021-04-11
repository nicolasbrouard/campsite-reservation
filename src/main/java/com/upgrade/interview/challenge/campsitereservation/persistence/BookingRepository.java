package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  long countByArrivalDateBetween(LocalDate start, LocalDate end);
  long countByDepartureDateBetween(LocalDate start, LocalDate end);
}
