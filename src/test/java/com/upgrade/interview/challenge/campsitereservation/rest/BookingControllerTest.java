package com.upgrade.interview.challenge.campsitereservation.rest;

import static com.upgrade.interview.challenge.campsitereservation.rest.BookingController.BASE_PATH;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.upgrade.interview.challenge.campsitereservation.Fixtures;
import com.upgrade.interview.challenge.campsitereservation.persistence.Booking;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingRepository;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
  @MockBean
  private BookingRepository bookingRepository;

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Test
  void addBooking() {
  }

  @Test
  void getBookingList_success() throws Exception {
    final List<Booking> bookingList = List.of(Fixtures.createValidBooking());
    final String bookingListJson = objectMapper.writeValueAsString(bookingList);
    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingList);
    mockMvc.perform(get(BASE_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(bookingListJson));
  }

  @Test
  void getBookingList_empty() throws Exception {
    final List<Booking> bookingList = List.of();
    final String bookingListJson = objectMapper.writeValueAsString(bookingList);
    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingList);
    mockMvc.perform(get(BASE_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(bookingListJson));
  }

  @Test
  void getBooking_success() throws Exception {
    final Booking booking = Fixtures.createValidBooking();
    final String bookingJson = objectMapper.writeValueAsString(booking);
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
    mockMvc.perform(get(BASE_PATH + "/" + booking.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(bookingJson));
  }

  @Test
  void getBooking_unknown() throws Exception {
    final Booking booking = Fixtures.createValidBooking();
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());
    mockMvc.perform(get(BASE_PATH + "/" + booking.getId()))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  void addBooking_success() throws Exception {
    final BookingInput bookingInput = Fixtures.createValidBookingInput();
    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
    final Booking expectedBooking = Booking.create(bookingInput);
    final String expectedBookingJson = objectMapper.writeValueAsString(bookingInput);
    when(bookingRepository.save(expectedBooking)).then(returnsFirstArg());
    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedBookingJson));
  }

  @Disabled("in progress")
  @Test
  void addBooking_tooEarly() throws Exception {
    final BookingInput bookingInput = Fixtures.createTooEarlyBookingInput();
    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isBadRequest())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString("The campsite can be reserved minimum 1 day(s) ahead of arrival")));
  }

  @Disabled("in progress")
  @Test
  void addBooking_notAvailable() throws Exception {
    final BookingInput bookingInput = Fixtures.createTooEarlyBookingInput();
    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
    when(bookingRepository.countByArrivalDateBetween(any(), any())).thenReturn(1L);
    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isConflict());
  }

  @Test
  void updateBooking() throws Exception {
    final BookingInput bookingInput = Fixtures.createValidBookingInput();
    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
    final Booking expectedBooking = Booking.create(bookingInput);
    final String expectedBookingJson = objectMapper.writeValueAsString(bookingInput);
    when(bookingRepository.save(expectedBooking)).then(returnsFirstArg());
    mockMvc.perform(put(BASE_PATH + "/1").contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isOk());
    // TODO more expect..
  }

  @Test
  void deleteBooking() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/1"))
        .andDo(print())
        .andExpect(status().isOk());
  }
}