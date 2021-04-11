package com.upgrade.interview.challenge.campsitereservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@EnableTransactionManagement
@SpringBootApplication
@Slf4j
public class CampsiteReservationApplication {

  public static void main(String[] args) {
    SpringApplication.run(CampsiteReservationApplication.class, args);
  }

}
