package com.pureinsights.exercise.backend.repository;

import com.pureinsights.exercise.backend.model.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Repository for {@link Media} entities
 * @author Andres Marenco
 */
public interface MediaRepository {
  /**
   * @param query the query to execute
   * @param pageRequest the page request configuration
   * @return a page with the results of the search
   */
  Page<Media> searchByTitle(String query, Pageable pageRequest);

  /**
   * @return A mapping of string keys and values with the key for each range and its count
   */
  Map<String, Integer> groupByRate();

  /**
   * @param genre the genre to search
   * @param range the code of the ratings range to restrict the search ( 1 = above, 2 = [6-8[, 3 = [4-6[, 4 = [2-4[, 5 = below )
   * @param pageRequest the page request configuration
   * @return a page with the results of the search
   */
  Page<Media> searchByTitleInRange(String genre, Integer range, Pageable pageRequest);
}
