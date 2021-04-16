package com.upgrade.interview.challenge.campsitereservation.rest;

import static com.upgrade.interview.challenge.campsitereservation.rest.BookingController.BASE_AVAILABLE_PATH;
import static com.upgrade.interview.challenge.campsitereservation.rest.BookingController.BASE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.upgrade.interview.challenge.campsitereservation.Fixtures;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingDate;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingDateRepository;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingEntity;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingRepository;
import com.upgrade.interview.challenge.campsitereservation.persistence.BookingService;

/**
 * Test the {@link BookingController}.
 * <p>Mock the Repository classes.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private BookingRepository bookingRepository;

  @MockBean
  private BookingDateRepository bookingDateRepository;

  @Autowired
  private MockMvc mockMvc;

  @SpyBean
  private BookingService bookingService;

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Test
  void getBookingList_success() throws Exception {
    final List<BookingEntity> bookingEntityList = List.of(Fixtures.createBookingEntityWithId());
    final List<Booking> bookingList = bookingEntityList.stream().map(Booking::createFrom).toList();
    final String bookingListJson = objectMapper.writeValueAsString(bookingList);
    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingEntityList);
    mockMvc.perform(get(BASE_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(bookingListJson));
  }

  @Test
  void getBookingList_empty() throws Exception {
    final List<BookingEntity> bookingEntityList = List.of();
    final String bookingListJson = objectMapper.writeValueAsString(bookingEntityList);
    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingEntityList);
    mockMvc.perform(get(BASE_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(bookingListJson));
  }

  @Test
  void getBooking_success() throws Exception {
    final BookingEntity bookingEntity = Fixtures.createBookingEntityWithId();
    final String bookingJson = objectMapper.writeValueAsString(Booking.createFrom(bookingEntity));
    when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));
    mockMvc.perform(get(BASE_PATH + "/" + bookingEntity.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(bookingJson));
  }

  @Test
  void getBooking_unknown() throws Exception {
    final BookingEntity bookingEntity = Fixtures.createBookingEntityWithId();
    when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.empty());
    mockMvc.perform(get(BASE_PATH + "/" + bookingEntity.getId()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString("Could not find booking with id " + bookingEntity.getId())));
  }

  @Test
  void addBooking_success() throws Exception {
    final Booking booking = Fixtures.createValidBooking();
    final String bookingJson = objectMapper.writeValueAsString(booking);
    final BookingEntity expectedBookingEntity = BookingEntity.createFrom(booking);
    final String expectedBookingJson = objectMapper.writeValueAsString(booking);
    when(bookingRepository.save(expectedBookingEntity)).then(returnsFirstArg());
    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedBookingJson));
  }

  private static Stream<Arguments> addBooking_invalid_source() {
    return Stream.of(
        Arguments.of(Fixtures.createTooEarlyBooking(), "The campsite can be reserved minimum 1 day(s) ahead of arrival."),
        Arguments.of(Fixtures.createTooLongBooking(), "The campsite can be reserved for maximum 3 days."),
        Arguments.of(Fixtures.createTooShortBooking(), "The campsite can be reserved for minimum 1 day."),
        Arguments.of(Fixtures.createTooLateBooking(), "The campsite can be reserved up to 31 day(s) in advance."),
        Arguments.of(Fixtures.createBookingWithDepartureBeforeArrival(), "Arrival date should be before departure date.")
    );
  }

  @ParameterizedTest
  @MethodSource("addBooking_invalid_source")
  void addBooking_invalid(Booking booking, String message) throws Exception {
    final String bookingJson = objectMapper.writeValueAsString(booking);
    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString(message)));
  }

  @Test
  void addBooking_internalServerError() throws Exception {
    final Booking booking = Fixtures.createValidBooking();
    final String bookingJson = objectMapper.writeValueAsString(booking);
    when(bookingRepository.save(any())).thenThrow(new RuntimeException("exception message"));
    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString("java.lang.RuntimeException: exception message")));
  }

  @Test
  void addBooking_notAvailable() throws Exception {
    final Booking booking = Fixtures.createValidBooking();
    final String bookingJson = objectMapper.writeValueAsString(booking);
    // A date within the booking dates is already booked
    final LocalDate alreadyBookedDate = booking.getArrivalDate();
    when(bookingDateRepository.findAllDatesBetween(any(), any()))
        .thenReturn(Stream.of(BookingDate.builder().date(alreadyBookedDate).build()));
    mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString(
            MessageFormat.format("Dates [{0}] are not available", alreadyBookedDate))));
  }

  @Test
  void updateBooking() throws Exception {
    final Booking oldBooking = Fixtures.createBooking(LocalDate.now().plusDays(2), 2);
    final Booking newBooking = Fixtures.createBooking(LocalDate.now().plusDays(2), 3);
    final String bookingJson = objectMapper.writeValueAsString(newBooking);
    final BookingEntity expectedBookingEntity = BookingEntity.createFrom(newBooking);
    when(bookingRepository.findById(any())).thenReturn(Optional.of(BookingEntity.createFrom(oldBooking)));
    when(bookingRepository.save(expectedBookingEntity)).then(returnsFirstArg());
    mockMvc.perform(put(BASE_PATH + "/1").contentType(MediaType.APPLICATION_JSON).content(bookingJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(bookingJson));
  }

  @Test
  void deleteBooking_success() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/1"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void deleteBooking_failure() throws Exception {
    doThrow(EmptyResultDataAccessException.class).when(bookingRepository).deleteById(any());
    mockMvc.perform(delete(BASE_PATH + "/1"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString("Could not find booking with id 1")));
  }

  @Test
  void getAvailabilitiesBetween_noBooking() throws Exception {
    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("start", "2021-01-28")
        .queryParam("end", "2021-02-03"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[2021-01-28, 2021-01-29, 2021-01-30, 2021-01-31, 2021-02-01, 2021-02-02]"));
  }

  @Test
  void getAvailabilitiesBetween_1Booking() throws Exception {
    final Stream<BookingDate> bookingDates = Fixtures.bookingDates("2021-01-29", 2);
    when(bookingDateRepository.fastFindAllDatesBetween(any(), any())).thenReturn(bookingDates);
    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("start", "2021-01-28")
        .queryParam("end", "2021-02-03"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[2021-01-28, 2021-01-31, 2021-02-01, 2021-02-02]"));
  }

  @Test
  void getAvailabilitiesBetween_2Bookings() throws Exception {
    final Stream<BookingDate> bookingDates = Stream.concat(
        Fixtures.bookingDates("2021-01-29", 2),
        Fixtures.bookingDates("2021-02-01", 1));
    when(bookingDateRepository.fastFindAllDatesBetween(any(), any())).thenReturn(bookingDates);
    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("start", "2021-01-28")
        .queryParam("end", "2021-02-03"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[2021-01-28, 2021-01-31, 2021-02-02]"));
  }

  @Test
  void getAvailabilitiesBetween_1Booking_overlapStartDate() throws Exception {
    final Stream<BookingDate> bookingDates = Fixtures.bookingDates("2021-01-27", 3);
    when(bookingDateRepository.fastFindAllDatesBetween(any(), any())).thenReturn(bookingDates);
    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("start", "2021-01-28")
        .queryParam("end", "2021-02-03"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[2021-01-30, 2021-01-31, 2021-02-01, 2021-02-02]"));
  }

  @Test
  void getAvailabilitiesBetween_1Booking_overlapEndDate() throws Exception {
    final Stream<BookingDate> bookingDates = Fixtures.bookingDates("2021-02-02", 3);
    when(bookingDateRepository.fastFindAllDatesBetween(any(), any())).thenReturn(bookingDates);
    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("start", "2021-01-28")
        .queryParam("end", "2021-02-03"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[2021-01-28, 2021-01-29, 2021-01-30, 2021-01-31, 2021-02-01]"));
  }

  @Test
  void getAvailabilitiesBetween_no_param() throws Exception {
    final ArgumentCaptor<LocalDate> argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);

    mockMvc.perform(get(BASE_AVAILABLE_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(bookingService).getAvailabilities(argumentCaptor.capture(), argumentCaptor.capture());
    assertThat(argumentCaptor.getAllValues()).containsExactly(LocalDate.now(), LocalDate.now().plusMonths(1));
  }

  @Test
  void getAvailabilitiesBetween_start_param() throws Exception {
    final ArgumentCaptor<LocalDate> argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
    final String start = "2021-01-28";
    final LocalDate startDate = LocalDate.parse(start);

    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("start", start))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(bookingService).getAvailabilities(argumentCaptor.capture(), argumentCaptor.capture());
    assertThat(argumentCaptor.getAllValues()).containsExactly(startDate, startDate.plusMonths(1));
  }

  @Test
  void getAvailabilitiesBetween_end_param() throws Exception {
    final ArgumentCaptor<LocalDate> argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
    final LocalDate endDate = LocalDate.now().plusDays(5);

    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("end", endDate.toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(bookingService).getAvailabilities(argumentCaptor.capture(), argumentCaptor.capture());
    assertThat(argumentCaptor.getAllValues()).containsExactly(LocalDate.now(), endDate);
  }

  @Test
  void getAvailabilitiesBetween_start_after_end() throws Exception {
    mockMvc.perform(get(BASE_AVAILABLE_PATH)
        .queryParam("start", "2021-01-02")
        .queryParam("end", "2021-01-01"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(containsString("Start date 2021-01-02 is after end date 2021-01-01")));
  }
}