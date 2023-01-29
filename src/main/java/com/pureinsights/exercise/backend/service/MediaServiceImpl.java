package com.pureinsights.exercise.backend.service;

import com.pureinsights.exercise.backend.model.Media;
import com.pureinsights.exercise.backend.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Default implementation of {@link MediaService}
 * @author Andres Marenco
 */
@Service
public class MediaServiceImpl implements MediaService {

  @Autowired
  private MediaRepository mediaRepository;


  @Override
  public Page<Media> searchTitle(String query, Pageable pageRequest) {
    return mediaRepository.searchByTitle(query, pageRequest);
  }

  @Override
  public Map<String, Integer> countRateRanges() {
    return mediaRepository.groupByRate();
  }

  @Override
  public Page<Media> searchGenreInRateRange(String genre, Integer range, Pageable pageRequest) {
    return mediaRepository.searchByTitleInRateRange(genre, range, pageRequest);
  }
}
