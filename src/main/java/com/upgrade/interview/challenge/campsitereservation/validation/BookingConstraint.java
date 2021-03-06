package com.upgrade.interview.challenge.campsitereservation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = BookingValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingConstraint {
  String message() default "The booking does not respect the constraints";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
