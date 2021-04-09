package com.upgrade.interview.challenge.campsitereservation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class ApiController {

  private final StudentRepository studentRepository;

  public ApiController(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  @GetMapping(path="/students")
  public List<Student> getStudents() {
    return studentRepository.findAll();
  }

  @GetMapping(path="/studentsflux")
  public Flux<Student> getStudents2() {
    return Flux.fromIterable(studentRepository.findAll());
  }
}
