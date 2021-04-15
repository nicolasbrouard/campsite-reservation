package com.upgrade.interview.challenge.campsitereservation.rest;

import java.time.DateTimeException;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Throwables;
import com.upgrade.interview.challenge.campsitereservation.exception.AlreadyBookedException;
import com.upgrade.interview.challenge.campsitereservation.exception.BadRequestException;
import com.upgrade.interview.challenge.campsitereservation.exception.BookingNotFoundException;
import com.upgrade.interview.challenge.campsitereservation.validation.BookingValidator;
import lombok.Builder;
import lombok.Value;

@ControllerAdvice
public class BookingControllerAdvice {

  @Value
  @Builder
  private static class ErrorResponse {
    HttpStatus status;
    String message;
  }

  private ErrorResponse errorHandler(HttpStatus status, Throwable throwable) {
    return ErrorResponse.builder()
        .status(status)
        .message(Throwables.getRootCause(throwable).getMessage())
        .build();
  }

  @ResponseBody
  @ExceptionHandler(BookingNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ErrorResponse notFoundHandler(BookingNotFoundException e) {
    return errorHandler(HttpStatus.NOT_FOUND, e);
  }

  @ResponseBody
  @ExceptionHandler(DateTimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse dateTimeExceptionHandler(DateTimeException e) {
    return errorHandler(HttpStatus.BAD_REQUEST, e);
  }

  @ResponseBody
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse badRequestHandler(BadRequestException e) {
    return errorHandler(HttpStatus.BAD_REQUEST, e);
  }

  @ResponseBody
  @ExceptionHandler(AlreadyBookedException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  ErrorResponse badRequestHandler(AlreadyBookedException e) {
    return errorHandler(HttpStatus.CONFLICT, e);
  }

  /**
   * Reformat the messages of the constraint violation (See {@link BookingValidator}.
   */
  @ResponseBody
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse constraintViolationHandler(BindException e) {
    final String message = e.getBindingResult().getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(". "))
        .concat(".");
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST)
        .message(message)
        .build();
  }

  @ResponseBody
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  ErrorResponse internalServerErrorHandler(RuntimeException e) {
    return ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .message(Throwables.getRootCause(e).toString())
        .build();
  }
}
