package com.pureinsights.exercise.backend.controller;

import com.pureinsights.exercise.backend.model.Media;
import com.pureinsights.exercise.backend.model.RateRangeOption;
import com.pureinsights.exercise.backend.service.MediaService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link MediaController}
 * @author Joseph Tenorio
 */
@SpringBootTest
@AutoConfigureMockMvc
class MediaControllerTest {

  private static final Random rng = new Random();
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MediaService mediaService;

  /**
   * Tests the endpoint defined in {@link MediaController#searchTitle(String, Pageable)}
   * when the there are five random media results
   */
  @Test
  void searchTitleTest() throws Exception {
    var pageRequest = Pageable.ofSize(10);
    var query = RandomStringUtils.randomAlphabetic(5);
    var mediaList = List.of(Media.builder().title(RandomStringUtils.randomAlphabetic(5)).build());
    var mediaPage = new PageImpl<>(mediaList, pageRequest, mediaList.size());
    when(mediaService.searchTitle(query, pageRequest)).thenReturn(mediaPage);

    mockMvc.perform(MockMvcRequestBuilders.get("/title/search")
        .queryParam("page", "0")
        .queryParam("size", String.valueOf(pageRequest.getPageSize()))
        .queryParam("q", query))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.content", hasSize(mediaList.size())))
        .andExpect(jsonPath("$.content[0].title").value(mediaList.get(0).getTitle()));
  }


  /**
   * Tests the endpoint defined in {@link MediaController#searchTitle(String, Pageable)}
   * when there are no results
   */
  @Test
  void searchTitleEmptyTest() throws Exception {
    var pageRequest = Pageable.ofSize(10);
    var query = RandomStringUtils.randomAlphabetic(5);
    when(mediaService.searchTitle(query, pageRequest)).thenReturn(Page.empty());

    mockMvc.perform(MockMvcRequestBuilders.get("/title/search")
        .queryParam("page", "0")
        .queryParam("size", String.valueOf(pageRequest.getPageSize()))
        .queryParam("q", query))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }

  /**
   * Tests the endpoint defined in {@link MediaController#countRateRanges()}
   */
  @Test
  void countRateRangesTest() throws Exception {
    Map<String, Integer> aggregationResult = new HashMap<>();
    RateRangeOption.VALID_REPRESENTATIONS.forEach(option -> aggregationResult
            .put(option, rng.nextInt() & Integer.MAX_VALUE));

    when(mediaService.countRateRanges()).thenReturn(aggregationResult);
    mockMvc.perform(MockMvcRequestBuilders.get("/rate/range-count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$." + RateRangeOption.ABOVE.getRepresentation())
                    .value(aggregationResult.get(RateRangeOption.ABOVE.getRepresentation())))
            .andExpect(jsonPath("$." +RateRangeOption.BETWEEN6AND8.getRepresentation())
                    .value(aggregationResult.get(RateRangeOption.BETWEEN6AND8.getRepresentation())))
            .andExpect(jsonPath("$." +RateRangeOption.BETWEEN4AND6.getRepresentation())
                    .value(aggregationResult.get(RateRangeOption.BETWEEN4AND6.getRepresentation())))
            .andExpect(jsonPath("$." +RateRangeOption.BETWEEN2AND4.getRepresentation())
                    .value(aggregationResult.get(RateRangeOption.BETWEEN2AND4.getRepresentation())))
            .andExpect(jsonPath("$." +RateRangeOption.BELLOW.getRepresentation())
                    .value(aggregationResult.get(RateRangeOption.BELLOW.getRepresentation())));
  }

  /**
   * Tests the endpoint defined in {@link MediaController#searchGenreInRateRange(String, Integer, Pageable)}
   * when the there are six random media results
   */
  @Test
  void searchGenreInRateRangeTest() throws Exception {
    var pageRequest = Pageable.ofSize(10);
    var query = RandomStringUtils.randomAlphabetic(5);
    var mediaList = List.of(Media.builder().genre(RandomStringUtils.randomAlphabetic(10)).build());
    var mediaPage = new PageImpl<>(mediaList, pageRequest, mediaList.size());
    int rangeCode = rng.nextInt(5);
    when(mediaService.searchGenreInRateRange(query, rangeCode, pageRequest )).thenReturn(mediaPage);

    mockMvc.perform(MockMvcRequestBuilders.get("/filter/genre-rate-range")
                    .queryParam("page", "0")
                    .queryParam("size", String.valueOf(pageRequest.getPageSize()))
                    .queryParam("genre", query)
                    .queryParam("rangeCode", String.valueOf(rangeCode)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content", hasSize(mediaList.size())))
            .andExpect(jsonPath("$.content[0].genre").value(mediaList.get(0).getGenre()));
  }


  /**
   * Tests the endpoint defined in {@link MediaController#searchGenreInRateRange(String, Integer, Pageable)}
   * when there are no results
   */
  @Test
  void searchGenreInRateRangeEmptyTest() throws Exception {
    var pageRequest = Pageable.ofSize(10);
    var query = RandomStringUtils.randomAlphabetic(5);
    int rangeCode = rng.nextInt(5);
    when(mediaService.searchGenreInRateRange(query, rangeCode, pageRequest)).thenReturn(Page.empty());

    mockMvc.perform(MockMvcRequestBuilders.get("/filter/genre-rate-range")
                    .queryParam("page", "0")
                    .queryParam("size", String.valueOf(pageRequest.getPageSize()))
                    .queryParam("genre", query)
                    .queryParam("rangeCode", String.valueOf(rangeCode)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());
  }
}
