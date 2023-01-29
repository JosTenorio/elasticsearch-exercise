package com.pureinsights.exercise.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Definition for a media document
 * @author Joseph Tenorio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {
    private String title;
    private String genre;
    private Float rate;
    private String year;

}
