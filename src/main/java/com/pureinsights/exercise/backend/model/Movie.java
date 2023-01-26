package com.pureinsights.exercise.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Definition for a Movie entity
 * @author Andres Marenco
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
  private String name;
  private int year;
}
