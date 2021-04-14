package com.upgrade.interview.challenge.campsitereservation.validation;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import com.upgrade.interview.challenge.campsitereservation.rest.Booking;
import com.upgrade.interview.challenge.campsitereservation.CampsiteConfiguration;

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
    final LocalDate arrivalDate = booking.getArrivalDate();
    final LocalDate departureDate = booking.getDepartureDate();
    final long daysAheadOfArrival = DAYS.between(LocalDate.now(), arrivalDate);

    context.disableDefaultConstraintViolation();
    boolean valid = true;

    // Arrival date should be before departure date
    if (departureDate.isBefore(arrivalDate)) {
      context.buildConstraintViolationWithTemplate(
          "Arrival date should be before departure date")
          .addConstraintViolation();
      valid = false;
    }

    // The campsite can be reserved for min 1 days.
    final long stayInDays = DAYS.between(arrivalDate, departureDate);
    if (stayInDays < 1) {
      context.buildConstraintViolationWithTemplate(
          "The campsite can be reserved for minimum 1 day")
          .addConstraintViolation();
      valid = false;
    }

    // The campsite can be reserved for max 3 days.
    final long maxStayInDays = configuration.getMaxStayInDays();
    if (stayInDays > maxStayInDays) {
      context.buildConstraintViolationWithTemplate(
          "The campsite can be reserved for maximum " + maxStayInDays + " days")
          .addConstraintViolation();
      valid = false;
    }

    // The campsite can be reserved minimum 1 day(s) ahead of arrival
    final long minDaysAheadOfArrival = configuration.getMinDaysAheadOfArrival();
    if (daysAheadOfArrival < minDaysAheadOfArrival) {
      context.buildConstraintViolationWithTemplate(
          "The campsite can be reserved minimum " + minDaysAheadOfArrival + " day(s) ahead of arrival")
          .addConstraintViolation();
      valid = false;
    }

    // The campsite can be reserved up to 1 month in advance.
    final long maxDaysAheadOfArrival = configuration.getMaxDaysAheadOfArrival();
    if (daysAheadOfArrival > maxDaysAheadOfArrival) {
      context.buildConstraintViolationWithTemplate(
          "The campsite can be reserved up to " + maxDaysAheadOfArrival + " day(s) in advance")
          .addConstraintViolation();
      valid = false;
    }
    return valid;
  }
}
