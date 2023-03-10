package com.pureinsights.exercise.backend.service;

import com.pureinsights.exercise.backend.model.RateRangeOption;
import com.pureinsights.exercise.backend.model.Media;
import com.pureinsights.exercise.backend.repository.MediaRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MediaServiceImpl}
 * @author Joseph Tenorio
 */
@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

  @InjectMocks
  private MediaServiceImpl mediaService;
  private static final Random rng = new Random();

  @Mock
  private MediaRepository mediaRepository;


  /**
   * Tests the {@link MediaServiceImpl#searchTitle(String, Pageable)} method
   */
  @Test
  void searchTitleTest() {
    var query = RandomStringUtils.randomAlphabetic(5);
    var pageRequest = Pageable.ofSize(10);
    var mediaResult = Page.<Media>empty(pageRequest);

    when(mediaRepository.searchByTitle(query, pageRequest)).thenReturn(mediaResult);

    var result = mediaService.searchTitle(query, pageRequest);
    assertEquals(mediaResult, result);
  }

  /**
   * Tests the {@link MediaServiceImpl#countRateRanges()} method
   */
  @Test
  void countRateRangesTest() {
    Map<String, Integer> aggregationResult = new HashMap<>();
    RateRangeOption.VALID_REPRESENTATIONS.forEach(option -> aggregationResult
            .put(option, rng.nextInt() & Integer.MAX_VALUE));
    when(mediaRepository.groupByRate()).thenReturn(aggregationResult);
    var result = mediaService.countRateRanges();
    assertEquals(aggregationResult, result);
  }

  /**
   * Tests the {@link MediaServiceImpl#searchGenreInRateRange(String, Integer, Pageable)} method
   */
  @Test
  void searchGenreInRateRangeTest () {
    var query = RandomStringUtils.randomAlphabetic(5);
    var pageRequest = Pageable.ofSize(10);
    int rangeCode = rng.nextInt(5);
    var mediaResult = Page.<Media>empty(pageRequest);
    when(mediaRepository.searchByTitleInRateRange(query,rangeCode, pageRequest)).thenReturn(mediaResult);
    var result = mediaService.searchGenreInRateRange(query, rangeCode, pageRequest);
    assertEquals(mediaResult, result);
  }
}
