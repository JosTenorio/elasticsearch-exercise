package com.pureinsights.exercise.backend.service;

import com.pureinsights.exercise.backend.repository.MediaRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MediaServiceImpl}
 * @author Andres Marenco
 */
@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

  @InjectMocks
  private MediaServiceImpl movieService;

  @Mock
  private MediaRepository mediaRepository;


  /**
   * Tests the {@link MediaServiceImpl#search(String, Pageable)} method
   */
  @Test
  void searchTest() {
    var query = RandomStringUtils.randomAlphabetic(5);
    var pageRequest = Pageable.ofSize(10);
    var movies = Page.<Movie>empty(pageRequest);

    when(mediaRepository.searchByTitle(query, pageRequest)).thenReturn(movies);

    var result = movieService.search(query, pageRequest);
    assertEquals(movies, result);
  }
}
