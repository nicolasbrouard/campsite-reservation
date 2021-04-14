package com.upgrade.interview.challenge.campsitereservation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
  /**
   * Returns a list of date between inclusiveStart and exclusiveEnd or an empty list.
   * Example: datesBetween('2021-04-22', '2021-04-25') returns:
   * - '2021-04-22'
   * - '2021-04-23'
   * - '2021-04-24'
   */
  public static List<LocalDate> datesBetween(LocalDate inclusiveStart, LocalDate exclusiveEnd) {
    if (inclusiveStart.isAfter(exclusiveEnd)) {
      return List.of();
    }
    final Stream<LocalDate> localDateStream = inclusiveStart.datesUntil(exclusiveEnd);
    return localDateStream.collect(Collectors.toList());
  }

  private Utils() {
  }
}
