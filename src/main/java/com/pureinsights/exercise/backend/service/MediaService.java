package com.pureinsights.exercise.backend.service;

import com.pureinsights.exercise.backend.model.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * A search service with methods to execute queries in the media collection
 * @author Andres Marenco
 */
public interface MediaService {
  /**
   * @param query the query to execute
   * @param pageRequest the page request configuration
   * @return a page with the results of the search
   */
  Page<Media> searchTitle(String query, Pageable pageRequest);

  /**
   * @return A mapping of string keys and values with the key for each range and its count as the value
   */
  Map<String, Integer> countRateRanges();

  /**
   * @param genre the genre to search
   * @param range the code of the ratings range to restrict the search ( 1 = above, 2 = [6-8[, 3 = [4-6[, 4 = [2-4[, 5 = below )
   * @param pageRequest the page request configuration
   * @return a page with the results of the search
   */
  Page<Media> searchGenreInRateRange(String genre, Integer range, Pageable pageRequest);
}
