package com.pureinsights.exercise.backend.controller;

import com.pureinsights.exercise.backend.model.Movie;
import com.pureinsights.exercise.backend.service.MovieService;
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

/**
 * REST Controller for the search endpoints
 * @author Andres Marenco
 */
@Tag(name = "Movie")
@RestController("/movie")
public class MovieController {

  @Autowired
  private MovieService movieService;


  @Operation(summary = "Search the movie collection", description = "Executes a search of a movie in the collection")
  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Movie>> search(@RequestParam("q") String query, @ParameterObject Pageable pageRequest) {
    return ResponseEntity.ok(movieService.search(query, pageRequest));
  }
}
