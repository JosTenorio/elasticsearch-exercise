package com.pureinsights.exercise.backend.controller;

import com.pureinsights.exercise.backend.model.Movie;
import com.pureinsights.exercise.backend.service.MovieService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link MovieController}
 * @author Andres Marenco
 */
@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MovieService movieService;


  /**
   * Tests the endpoint defined in {@link MovieController#search(String, Pageable)}
   */
  @Test
  void searchTest() throws Exception {
    var pageRequest = Pageable.ofSize(10);
    var query = RandomStringUtils.randomAlphabetic(5);
    var movieList = List.of(Movie.builder().name(RandomStringUtils.randomAlphabetic(5)).build());
    var moviePage = new PageImpl<>(movieList, pageRequest, movieList.size());
    when(movieService.search(query, pageRequest)).thenReturn(moviePage);

    mockMvc.perform(MockMvcRequestBuilders.get("/search")
        .queryParam("page", "0")
        .queryParam("size", String.valueOf(pageRequest.getPageSize()))
        .queryParam("q", query))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.content", hasSize(movieList.size())))
        .andExpect(jsonPath("$.content[0].name").value(movieList.get(0).getName()));
  }


  /**
   * Tests the endpoint defined in {@link MovieController#search(String, Pageable)}
   * when there are no results
   */
  @Test
  void search_empty_Test() throws Exception {
    var pageRequest = Pageable.ofSize(10);
    var query = RandomStringUtils.randomAlphabetic(5);
    when(movieService.search(query, pageRequest)).thenReturn(Page.empty());

    mockMvc.perform(MockMvcRequestBuilders.get("/search")
        .queryParam("page", "0")
        .queryParam("size", String.valueOf(pageRequest.getPageSize()))
        .queryParam("q", query))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }
}
