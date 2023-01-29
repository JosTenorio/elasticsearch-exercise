package com.pureinsights.exercise.backend.repository;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link MediaRepositoryImpl}
 * @author Joseph Tenorio
 */
@ExtendWith(MockitoExtension.class)
class MediaRepositoryImplTest {

  @InjectMocks
  private MediaRepositoryImpl mediaRepository;

  /**
   * Tests the {@link MediaRepositoryImpl#init()} method, making sure the index is created
   * and has all documents of the test dataset.
   */
  @Test
  void initTest() throws Exception {
    mediaRepository.init();
    SearchResponse<Void> response = mediaRepository.client.search(s -> s
                    .index(MediaRepositoryImpl.INDEX)
                    .size(0)
                    .query(q -> q.matchAll(ma -> ma)), Void.class);
    assertEquals(48, response.hits().total().value());
  }
}
