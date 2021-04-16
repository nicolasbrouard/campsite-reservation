package com.upgrade.interview.challenge.campsitereservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UtilsTest {

  private static Stream<Arguments> datesBetweenSource() {
    return Stream.of(
        Arguments.of("2021-01-01", "2021-01-03", List.of("2021-01-01", "2021-01-02")),
        Arguments.of("2021-01-03", "2021-01-01", List.of()),
        Arguments.of("2021-04-22", "2021-04-25", List.of("2021-04-22", "2021-04-23", "2021-04-24")),
        Arguments.of("2021-04-22", "2021-04-22", List.of()),
        Arguments.of("2021-04-22", "2021-04-23", List.of("2021-04-22")),
        Arguments.of("2021-04-29", "2021-05-02", List.of("2021-04-29", "2021-04-30", "2021-05-01")));

  }

  @ParameterizedTest
  @MethodSource("datesBetweenSource")
  void datesBetween(String startInclusive, String endExclusive, List<String> output) {
    final LocalDate start = LocalDate.parse(startInclusive);
    final LocalDate end = LocalDate.parse(endExclusive);
    final List<LocalDate> expectedOutput = output
        .stream()
        .map(LocalDate::parse)
        .toList();

    final List<LocalDate> localDates = Utils.datesBetween(start, end);

    assertThat(localDates).containsAnyElementsOf(expectedOutput);
  }
}
