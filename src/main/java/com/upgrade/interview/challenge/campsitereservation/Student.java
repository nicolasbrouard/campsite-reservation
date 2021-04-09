package com.upgrade.interview.challenge.campsitereservation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Student {
  @Id
  @GeneratedValue
  private long id;
  private String name;
  private String passportNumber;
}
