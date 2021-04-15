package com.upgrade.interview.challenge.campsitereservation.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.upgrade.interview.challenge.campsitereservation.Fixtures;
import com.upgrade.interview.challenge.campsitereservation.rest.Booking;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

  @Autowired
  private BookingValidator bookingValidator;

  @Mock
  private ConstraintValidatorContext context;

  private static Stream<Arguments> invalidBookingSource() {
    return Stream.of(
        Arguments.of(Fixtures.createTooEarlyBooking(), false),
        Arguments.of(Fixtures.createTooLateBooking(), false),
        Arguments.of(Fixtures.createTooLongBooking(), false),
        Arguments.of(Fixtures.createTooShortBooking(), false)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidBookingSource")
  void testInvalid(Booking booking) {
    when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

    assertThat(bookingValidator.isValid(booking, context)).isFalse();
  }

  @Test
  void testValid() {
    assertThat(bookingValidator.isValid(Fixtures.createValidBooking(), context)).isTrue();
  }
}