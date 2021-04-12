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
import com.upgrade.interview.challenge.campsitereservation.rest.BookingInput;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

  @Autowired
  private BookingValidator bookingValidator;

  @Mock
  private ConstraintValidatorContext context;

  private static Stream<Arguments> invalid_source() {
    return Stream.of(
        Arguments.of(Fixtures.createTooEarlyBookingInput(), false),
        Arguments.of(Fixtures.createTooLateBookingInput(), false),
        Arguments.of(Fixtures.createTooLongBookingInput(), false),
        Arguments.of(Fixtures.createTooShortBookingInput(), false)
    );
  }

  @ParameterizedTest
  @MethodSource("invalid_source")
  void testInvalid(BookingInput bookingInput) {
    when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

    assertThat(bookingValidator.isValid(bookingInput, context)).isFalse();
  }

  @Test
  void testValid() {
    assertThat(bookingValidator.isValid(Fixtures.createValidBookingInput(), context)).isTrue();
  }
}