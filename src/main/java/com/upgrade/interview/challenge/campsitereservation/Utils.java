package com.upgrade.interview.challenge.campsitereservation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
  private Utils() {
  }

  /**
   * Returns a list of dates between startInclusive and endExclusive or an empty list.
   * <p>Example:</p>
   * <pre>datesBetween('2021-04-22', '2021-04-25') returns:
   *  - '2021-04-22'
   *  - '2021-04-23'
   *  - '2021-04-24'</pre>
   *
   * @return a list of dates.
   */
  public static List<LocalDate> datesBetween(LocalDate startInclusive, LocalDate endExclusive) {
    if (startInclusive.isAfter(endExclusive)) {
      return List.of();
    }
    return startInclusive.datesUntil(endExclusive).collect(Collectors.toList());
  }
}
