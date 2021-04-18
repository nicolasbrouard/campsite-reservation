package com.upgrade.interview.challenge.campsitereservation.validation;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.upgrade.interview.challenge.campsitereservation.CampsiteConfiguration;
import com.upgrade.interview.challenge.campsitereservation.rest.Booking;

@Component
public class BookingValidator implements ConstraintValidator<BookingConstraint, Booking> {
  private final CampsiteConfiguration configuration;

  public BookingValidator(CampsiteConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void initialize(BookingConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(Booking booking, ConstraintValidatorContext context) {
    final var arrivalDate = booking.getArrivalDate();
    final var departureDate = booking.getDepartureDate();

    if (arrivalDate == null || departureDate == null) {
      // null values are valid
      return true;
    }

    final var daysAheadOfArrival = DAYS.between(LocalDate.now(), arrivalDate);

    context.disableDefaultConstraintViolation();
    var valid = true;

    // Arrival date should be before departure date
    if (departureDate.isBefore(arrivalDate)) {
      valid = invalidErrorMessage(context, "Arrival date should be before departure date");
    }

    // The campsite can be reserved for min 1 days.
    final var stayInDays = DAYS.between(arrivalDate, departureDate);
    if (stayInDays < 1) {
      valid = invalidErrorMessage(context, "The campsite can be reserved for minimum 1 day");
    }

    // The campsite can be reserved for max 3 days.
    final var maxStayInDays = configuration.getMaxBookingDurationInDays();
    if (stayInDays > maxStayInDays) {
      valid = invalidErrorMessage(context, "The campsite can be reserved for maximum " + maxStayInDays + " days");
    }

    // The campsite can be reserved minimum 1 day(s) ahead of arrival
    final var minDaysAheadOfArrival = configuration.getMinDaysAheadOfArrival();
    if (daysAheadOfArrival < minDaysAheadOfArrival) {
      valid = invalidErrorMessage(context, "The campsite can be reserved minimum " + minDaysAheadOfArrival + " day(s) ahead of arrival");
    }

    // The campsite can be reserved up to 1 month in advance.
    final var maxDaysAheadOfArrival = configuration.getMaxDaysAheadOfArrival();
    if (daysAheadOfArrival > maxDaysAheadOfArrival) {
      valid = invalidErrorMessage(context, "The campsite can be reserved up to " + maxDaysAheadOfArrival + " day(s) in advance");
    }
    return valid;
  }

  private boolean invalidErrorMessage(ConstraintValidatorContext context, String message) {
    context
        .buildConstraintViolationWithTemplate(message)
        .addConstraintViolation();
    return false;
  }

}
