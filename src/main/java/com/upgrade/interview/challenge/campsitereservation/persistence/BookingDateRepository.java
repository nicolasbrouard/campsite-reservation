package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDateRepository extends JpaRepository<BookingDate, LocalDate> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select d from #{#entityName} d where d.date >= ?1 and d.date < ?2")
  List<BookingDate> findAllDatesBetween(LocalDate inclusiveStart, LocalDate exclusiveEnd);
}
