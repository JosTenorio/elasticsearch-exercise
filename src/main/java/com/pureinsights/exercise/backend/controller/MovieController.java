package com.pureinsights.exercise.backend.controller;

import com.pureinsights.exercise.backend.model.Media;
import com.pureinsights.exercise.backend.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST Controller for the search endpoints
 * @author Andres Marenco
 */
@Tag(name = "media")
@RestController("/media")
public class MovieController {

  @Autowired
  private MediaService mediaService;


  @Operation(summary = "Search the movie collection", description = "Executes a search of a movie in the collection")
  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Media>> search(@RequestParam("q") String query, @ParameterObject Pageable pageRequest) {
    return ResponseEntity.ok(mediaService.search(query, pageRequest));
  }

  @Operation(summary = "Count titles grouped by rate ranges",
          description = "Counts the different titles found in each range of rates: " +
                  "All above, [6-8[, [4-6[, [2-4[, All below")
  @GetMapping(value = "/rate-range-count", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Integer>> countRateRanges() {
    return ResponseEntity.ok(mediaService.countRateRanges());
  }

  @Operation(summary = "Filter the movie collection by genre and rate range",
          description = "Queries for all titles belonging to a given range whose rating is in a given range")
  @GetMapping(value = "/genre-rate-filter", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Media>> searchGenreInRateRange(@RequestParam("genre") String genre, @RequestParam("range") Integer range,
                                            @ParameterObject Pageable pageRequest) {
    return ResponseEntity.ok(mediaService.searchGenreInRateRange(genre, range, pageRequest));
  }
}
