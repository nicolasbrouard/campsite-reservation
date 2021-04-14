//package com.upgrade.interview.challenge.campsitereservation.rest;
//
//import static com.upgrade.interview.challenge.campsitereservation.rest.BookingController.BASE_AVAILABLE_PATH;
//import static com.upgrade.interview.challenge.campsitereservation.rest.BookingController.BASE_PATH;
//import static org.hamcrest.Matchers.containsString;
//import static org.mockito.AdditionalAnswers.returnsFirstArg;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Stream;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.upgrade.interview.challenge.campsitereservation.Fixtures;
//import com.upgrade.interview.challenge.campsitereservation.persistence.BookingEntity;
//import com.upgrade.interview.challenge.campsitereservation.persistence.BookingRepository;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class BookingControllerTest {
//  private final ObjectMapper objectMapper = new ObjectMapper();
//  @MockBean
//  private BookingRepository bookingRepository;
//  @Autowired
//  private MockMvc mockMvc;
//
//  @BeforeEach
//  void setUp() {
//    objectMapper.registerModule(new JavaTimeModule());
//    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//  }
//
//  @Test
//  void addBooking() {
//  }
//
//  @Test
//  void getBookingList_success() throws Exception {
//    final List<BookingEntity> bookingEntityList = List.of(Fixtures.createValidBooking());
//    final String bookingListJson = objectMapper.writeValueAsString(bookingEntityList);
//    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingEntityList);
//    mockMvc.perform(get(BASE_PATH))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json(bookingListJson));
//  }
//
//  @Test
//  void getBookingList_empty() throws Exception {
//    final List<BookingEntity> bookingEntityList = List.of();
//    final String bookingListJson = objectMapper.writeValueAsString(bookingEntityList);
//    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingEntityList);
//    mockMvc.perform(get(BASE_PATH))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json(bookingListJson));
//  }
//
//  @Test
//  void getBooking_success() throws Exception {
//    final BookingEntity bookingEntity = Fixtures.createValidBooking();
//    final String bookingJson = objectMapper.writeValueAsString(bookingEntity);
//    when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));
//    mockMvc.perform(get(BASE_PATH + "/" + bookingEntity.getId()))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json(bookingJson));
//  }
//
//  @Test
//  void getBooking_unknown() throws Exception {
//    final BookingEntity bookingEntity = Fixtures.createValidBooking();
//    when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.empty());
//    mockMvc.perform(get(BASE_PATH + "/" + bookingEntity.getId()))
//        .andDo(print())
//        .andExpect(status().isNotFound());
//  }
//
//  @Test
//  void addBooking_success() throws Exception {
//    final BookingInput bookingInput = Fixtures.createValidBookingInput();
//    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
//    final BookingEntity expectedBookingEntity = BookingEntity.create(bookingInput);
//    final String expectedBookingJson = objectMapper.writeValueAsString(bookingInput);
//    when(bookingRepository.save(expectedBookingEntity)).then(returnsFirstArg());
//    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json(expectedBookingJson));
//  }
//
//  @Disabled("in progress")
//  @Test
//  void addBooking_tooEarly() throws Exception {
//    final BookingInput bookingInput = Fixtures.createTooEarlyBookingInput();
//    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
//    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
//        .andDo(print())
//        .andExpect(status().isBadRequest())
////        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().string(containsString("The campsite can be reserved minimum 1 day(s) ahead of arrival")));
//  }
//
//  @Disabled("in progress")
//  @Test
//  void addBooking_notAvailable() throws Exception {
//    final BookingInput bookingInput = Fixtures.createTooEarlyBookingInput();
//    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
//    when(bookingRepository.countByArrivalDateBetween(any(), any())).thenReturn(1L);
//    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
//        .andDo(print())
//        .andExpect(status().isConflict());
//  }
//
//  @Test
//  void updateBooking() throws Exception {
//    final BookingInput bookingInput = Fixtures.createValidBookingInput();
//    final String bookingJson = objectMapper.writeValueAsString(bookingInput);
//    final BookingEntity expectedBookingEntity = BookingEntity.create(bookingInput);
//    final String expectedBookingJson = objectMapper.writeValueAsString(bookingInput);
//    when(bookingRepository.save(expectedBookingEntity)).then(returnsFirstArg());
//    mockMvc.perform(put(BASE_PATH + "/1").contentType(MediaType.APPLICATION_JSON).content(bookingJson))
//        .andDo(print())
//        .andExpect(status().isOk());
//    // TODO more expect..
//  }
//
//  @Test
//  void deleteBooking() throws Exception {
//    mockMvc.perform(delete(BASE_PATH + "/1"))
//        .andDo(print())
//        .andExpect(status().isOk());
//  }
//
//  @Test
//  void getAvailabilitiesBetween_noBooking() throws Exception {
//    mockMvc.perform(get(BASE_AVAILABLE_PATH)
//        .queryParam("start", "2021-01-28")
//        .queryParam("end", "2021-02-03"))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json("[2021-01-28, 2021-01-29, 2021-01-30, 2021-01-31, 2021-02-01, 2021-02-02]"));
//  }
//
//  @Test
//  void getAvailabilitiesBetween_1Booking() throws Exception {
//    final BookingEntity bookingEntity = Fixtures.createValidBooking(LocalDate.parse("2021-01-29"), 2);
//    when(bookingRepository.findAllByArrivalDateBetween(any(), any())).thenReturn(Stream.of(bookingEntity));
//    when(bookingRepository.findAllByDepartureDateBetween(any(), any())).thenReturn(Stream.of(bookingEntity));
//    mockMvc.perform(get(BASE_AVAILABLE_PATH)
//        .queryParam("start", "2021-01-28")
//        .queryParam("end", "2021-02-03"))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json("[2021-01-28, 2021-01-31, 2021-02-01, 2021-02-02]"));
//  }
//
//  @Test
//  void getAvailabilitiesBetween_2Bookings() throws Exception {
//    final BookingEntity bookingEntity1 = Fixtures.createValidBooking(LocalDate.parse("2021-01-29"), 2);
//    final BookingEntity bookingEntity2 = Fixtures.createValidBooking(LocalDate.parse("2021-02-01"), 1);
//    when(bookingRepository.findAllByArrivalDateBetween(any(), any())).thenReturn(Stream.of(bookingEntity1, bookingEntity2));
//    when(bookingRepository.findAllByDepartureDateBetween(any(), any())).thenReturn(Stream.of(bookingEntity1, bookingEntity2));
//    mockMvc.perform(get(BASE_AVAILABLE_PATH)
//        .queryParam("start", "2021-01-28")
//        .queryParam("end", "2021-02-03"))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json("[2021-01-28, 2021-01-31, 2021-02-02]"));
//  }
//
//  @Test
//  void getAvailabilitiesBetween_1Booking_overlapStartDate() throws Exception {
//    final BookingEntity bookingEntity = Fixtures.createValidBooking(LocalDate.parse("2021-01-27"), 3);
//    when(bookingRepository.findAllByArrivalDateBetween(any(), any())).thenReturn(Stream.of(bookingEntity));
//    when(bookingRepository.findAllByDepartureDateBetween(any(), any())).thenReturn(Stream.empty());
//    mockMvc.perform(get(BASE_AVAILABLE_PATH)
//        .queryParam("start", "2021-01-28")
//        .queryParam("end", "2021-02-03"))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json("[2021-01-30, 2021-01-31, 2021-02-01, 2021-02-02]"));
//  }
//
//  @Test
//  void getAvailabilitiesBetween_1Booking_overlapEndDate() throws Exception {
//    final BookingEntity bookingEntity = Fixtures.createValidBooking(LocalDate.parse("2021-02-02"), 3);
//    when(bookingRepository.findAllByArrivalDateBetween(any(), any())).thenReturn(Stream.empty());
//    when(bookingRepository.findAllByDepartureDateBetween(any(), any())).thenReturn(Stream.of(bookingEntity));
//    mockMvc.perform(get(BASE_AVAILABLE_PATH)
//        .queryParam("start", "2021-01-28")
//        .queryParam("end", "2021-02-03"))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().json("[2021-01-28, 2021-01-29, 2021-01-30, 2021-01-31, 2021-02-01]"));
//  }
//}