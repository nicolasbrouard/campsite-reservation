package com.upgrade.interview.challenge.campsitereservation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = BookingValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingConstraint {
  String message() default "Invalid phone number";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
