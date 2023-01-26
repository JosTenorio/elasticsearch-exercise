package com.pureinsights.exercise.backend.service;

import com.pureinsights.exercise.backend.model.Movie;
import com.pureinsights.exercise.backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link MovieService}
 * @author Andres Marenco
 */
@Service
public class MovieServiceImpl implements MovieService {

  @Autowired
  private MovieRepository movieRepository;


  @Override
  public Page<Movie> search(String query, Pageable pageRequest) {
    return movieRepository.search(query, pageRequest);
  }
}
