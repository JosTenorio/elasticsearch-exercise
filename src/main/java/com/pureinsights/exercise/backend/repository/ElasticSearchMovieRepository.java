package com.pureinsights.exercise.backend.repository;

import com.pureinsights.exercise.backend.model.Movie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.Closeable;
import java.util.ArrayList;

/**
 * ElasticSearch implementation of {@link MovieRepository}
 * @author Joseph Tenorio
 */
@Repository("Elastic")
@Qualifier("Elastic")
public class ElasticSearchMovieRepository implements MovieRepository, Closeable {

    @Override
    public Page<Movie> search(String query, Pageable pageRequest) {
        ArrayList<Movie> gfg = new ArrayList<>() {
            {
                add(new Movie("Name1", 1523));
                add(new Movie("Name2", 1523));
                add(new Movie("Name3", 1523));
            }
        };
        return new PageImpl<>(gfg, pageRequest, 3);
    }

    @Override
    public void close() {
        System.out.println("Repository Closed");
    }
}

