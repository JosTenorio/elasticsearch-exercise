package com.pureinsights.exercise.backend.controller;

import com.pureinsights.exercise.backend.model.Media;
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

import java.util.List;

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

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MediaService mediaService;


  /**
   * Tests the endpoint defined in {@link MediaController#searchTitle(String, Pageable)}
   * when the there are five random media results
   */
  @Test
  void searchTest() throws Exception {
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
  void search_empty_Test() throws Exception {
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
}
