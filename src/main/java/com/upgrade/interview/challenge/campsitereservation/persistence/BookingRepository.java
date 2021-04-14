package com.upgrade.interview.challenge.campsitereservation.persistence;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  <S extends Booking> S save(S entity);
}
