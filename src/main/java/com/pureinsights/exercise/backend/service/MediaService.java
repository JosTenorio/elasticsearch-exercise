package com.pureinsights.exercise.backend.service;

import com.pureinsights.exercise.backend.model.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * A search service with methods to execute queries in the media collection
 * @author Joseph Tenorio
 */
public interface MediaService {
  /**
   * @param query the query to execute
   * @param pageRequest the page request configuration
   * @return a page with the results of the search
   */
  Page<Media> searchTitle(String query, Pageable pageRequest);

  /**
   * @return A mapping of string keys and integer values with the key for each rate range and its count as the value
   */
  Map<String, Integer> countRateRanges();

  /**
   * @param genre the genre to search
   * @param rangeCode the code of the ratings range to restrict the search ( 0 = above, 1 = [6-8[,
   *                 2 = [4-6[, 3 = [2-4[, 4 = below )
   * @param pageRequest the page request configuration
   * @return a page with the results of the search
   */
  Page<Media> searchGenreInRateRange(String genre, Integer rangeCode, Pageable pageRequest);
}
