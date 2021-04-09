package com.upgrade.interview.challenge.campsitereservation;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Booking {
  @Id
  @GeneratedValue
  private long id;

  private String email;

  private String name;

  @Temporal(TemporalType.TIMESTAMP)
  private Date arrivalDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date departureDate;
}
