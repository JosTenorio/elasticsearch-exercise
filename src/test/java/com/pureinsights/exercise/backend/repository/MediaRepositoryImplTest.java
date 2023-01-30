package com.pureinsights.exercise.backend.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link MediaRepositoryImpl}
 * @author Joseph Tenorio
 */
@ExtendWith(MockitoExtension.class)
class MediaRepositoryImplTest {
  @InjectMocks
  private MediaRepositoryImpl mediaRepository;

  /**
   * Tests the {@link MediaRepositoryImpl#searchByTitleInRateRange(String, Integer, Pageable)} )} method
   * when an {@link IllegalArgumentException} is thrown
   */
  @Test
  void searchByTitleInRateRange_illegalArgumentException_Test() {
    var pageRequest = PageRequest.of(1, 2);
    var genre = RandomStringUtils.randomAlphabetic(5);
    assertThrows(IllegalArgumentException.class, () -> mediaRepository
            .searchByTitleInRateRange(genre, -8, pageRequest));
  }

}
