package com.upgrade.interview.challenge.campsitereservation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "campsite")
@Configuration
@Getter
@Setter
public class CampsiteConfiguration {
  private long maxBookingDurationInDays;
  private long minDaysAheadOfArrival;
  private long maxDaysAheadOfArrival;
}
