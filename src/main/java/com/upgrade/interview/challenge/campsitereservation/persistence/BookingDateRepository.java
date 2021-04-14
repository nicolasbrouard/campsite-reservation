package com.upgrade.interview.challenge.campsitereservation.persistence;

import java.time.LocalDate;
import java.util.stream.Stream;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDateRepository extends JpaRepository<BookingDate, LocalDate> {

  String QUERY_FIND_DATES_BETWEEN = "select d from #{#entityName} d where d.date >= ?1 and d.date < ?2";

  @Lock(LockModeType.PESSIMISTIC_WRITE) // Use select for update
  @Query(QUERY_FIND_DATES_BETWEEN)
  Stream<BookingDate> findAllDatesBetween(LocalDate startInclusive, LocalDate endExclusive);

  @Query(QUERY_FIND_DATES_BETWEEN)
  Stream<BookingDate> fastFindAllDatesBetween(LocalDate startInclusive, LocalDate endExclusive);
}
