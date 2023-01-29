package com.pureinsights.exercise.backend.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.pureinsights.exercise.backend.model.Media;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ElasticSearch implementation of {@link MediaRepository}
 * @author Joseph Tenorio
 */
@Repository
public class MediaRepositoryImpl implements MediaRepository {
    private static final String CLUSTER_HOST = "127.0.0.1";
    private static final Integer CLUSTER_PORT = 9200;
    private static final String INDEX = "imdb_media-test";
    private static final String TITLE_FIELD = "title";
    private static final String RATE_FIELD = "rate";
    private static final String GENRE_FIELD = "genre";

    private static final RestClient restClient = RestClient.builder(
            new HttpHost(CLUSTER_HOST, CLUSTER_PORT)).build();

    private static final ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

    private static final ElasticsearchClient client = new ElasticsearchClient(transport);

    @Override
    public Page<Media> searchByTitle(String query, Pageable pageRequest) {
        SearchResponse<Media> search;
        int offset = pageRequest.getPageNumber() * pageRequest.getPageSize();
        try {
            search = client.search(s -> s.index(INDEX)
                                         .query(q -> q
                                                 .match(t -> t
                                                         .field(TITLE_FIELD)
                                                         .query(query)
                                                 )
                                         )
                            .from(offset)
                            .size(pageRequest.getPageSize()),
                    Media.class);
            List<Media> results = new ArrayList<>();
            search.hits().hits().forEach(e -> results.add(e.source()));
            return new PageImpl<>(results, pageRequest, search.hits().total().value());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Map<String, Integer> groupByRate() {
        HashMap<String, Integer> body = new HashMap<>();

        // Dummy data
        body.put("England", 5);
        body.put("Germany", 3);
        body.put("Norway", 8);
        body.put("USA", 4);

        return body;
    }

    @Override
    public Page<Media> searchByTitleInRange(String genre, Integer range, Pageable pageRequest) {
        SearchResponse<Media> search;
        int offset = pageRequest.getPageNumber() * pageRequest.getPageSize();
        try {
            search = client.search(s -> s.index(INDEX)
                            .query(q -> q
                                    .match(t -> t
                                            .field(GENRE_FIELD)
                                            .fuzziness("0")
                                            .query(genre)
                                    )
                            )
                            .from(offset)
                            .size(pageRequest.getPageSize()),
                    Media.class);
            List<Media> results = new ArrayList<>();
            search.hits().hits().forEach(e -> results.add(e.source()));
            return new PageImpl<>(results, pageRequest, search.hits().total().value());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

