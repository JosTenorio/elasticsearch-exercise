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
 * REST controller for media queries
 * @author Joseph Tenorio
 */
@Tag(name = "media")
@RestController("/media")
public class MediaController {

  @Autowired
  private MediaService mediaService;

  @Operation(summary = "Search the media index by title",
          description = "Executes a search of media by title")
  @GetMapping(value = "/title/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Media>> searchTitle(@RequestParam("q") String query, @ParameterObject Pageable pageRequest) {
    return ResponseEntity.ok(mediaService.searchTitle(query, pageRequest));
  }

  @Operation(summary = "Count the amount of media documents in ranges of their rate",
          description = "Counts the amount of different media found in each rate range available")
  @GetMapping(value = "/rate/range-count", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Integer>> countRateRanges() {
    return ResponseEntity.ok(mediaService.countRateRanges());
  }

  @Operation(summary = "Filter the movie collection by genre and rate range",
          description = "Queries for all media documents belonging to a given genre and the rate range" +
                  " corresponding to the rangeCode. Ranges codes: above 8 = 0, [6-8[ = 1, [4-6[ = 2, " +
                  "[2-4[ = 3, bellow 2 = 4")
  @GetMapping(value = "/filter/genre-rate-range", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> searchGenreInRateRange(@RequestParam("genre") String genre,
                                                            @RequestParam("rangeCode") Integer rangeCode,
                                                            @ParameterObject Pageable pageRequest) {
    try {
      return ResponseEntity.ok(mediaService.searchGenreInRateRange(genre, rangeCode, pageRequest));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body("{ \"error\": \"" + ex.getMessage() + " \"}");
    }
  }
}
