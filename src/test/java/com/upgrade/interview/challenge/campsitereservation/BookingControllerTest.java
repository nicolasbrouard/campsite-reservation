package com.upgrade.interview.challenge.campsitereservation;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.upgrade.interview.challenge.campsitereservation.persistence.BookingRepository;
import com.upgrade.interview.challenge.campsitereservation.rest.BookingController;

@WebMvcTest
@AutoConfigureMockMvc
class BookingControllerTest {
  @MockBean
  private BookingRepository bookingRepository;

  @Autowired
  BookingController bookingController;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void addBooking() {
  }

  @Test
  public void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {
    MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, Charset.forName("UTF-8"));
    String user = "{\"name\": \"bob\", \"email\" : \"bob@domain.com\"}";
    mockMvc.perform(MockMvcRequestBuilders.post("/users")
        .content(user)
        .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content()
            .contentType(textPlainUtf8));
  }
}