package com.pureinsights.exercise.backend.repository;

import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import com.opencsv.CSVReader;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.pureinsights.exercise.backend.model.RateRangeOption;
import com.pureinsights.exercise.backend.model.Media;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ElasticSearch implementation of {@link MediaRepository}
 * @author Joseph Tenorio
 */
@Repository
public class MediaRepositoryImpl implements MediaRepository {
    /** The CSV source file for the media index */
    private static final String MEDIA_DATA = "imdb.csv";
    /** The host address of the elasticsearch cluster */
    private static final String CLUSTER_HOST = "127.0.0.1";
    /** The port of through which the elasticsearch node can be accessed */
    private static final Integer CLUSTER_PORT = 9200;
    /** The name of the elasticsearch index */
    protected static final String INDEX = "imdb_media";
    /** Field with the media title */
    private static final String TITLE_FIELD = "title";
    /** Field with the media rate */
    private static final String RATE_FIELD = "rate";
    /** Field with the media genres */
    private static final String GENRE_FIELD = "genre";
    /** The  REST client used to communicate with the elasticsearch cluster */
    private final RestClient restClient = RestClient.builder(
            new HttpHost(CLUSTER_HOST, CLUSTER_PORT)).build();
    /** The transport used to communicate with the REST client*/
    private final ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());
    /** The elasticsearch client used in the repository to search the cluster*/
    protected final ElasticsearchClient client = new ElasticsearchClient(transport);

    @PostConstruct
    void init() throws IOException {
        if (!client.indices().exists(existsIndexRequest -> existsIndexRequest.index(INDEX)).value()) {
            client.indices().create(createIndexRequest -> createIndexRequest.index(INDEX));
            CSVReader dataReader = new CSVReader(new InputStreamReader(Objects.requireNonNull(getClass()
                    .getClassLoader()
                    .getResourceAsStream(MEDIA_DATA)
            )));
            dataReader.skip(1);
            BulkRequest.Builder br = new BulkRequest.Builder();
            for (var col : dataReader) {
                // The rate column is given a value of -1 when there is no rate
                // to keep values as floats. Important to keep these values out of rage counts.
                Float rate = Objects.equals(col[2], "No Rate") ? -1.0f : Float.parseFloat(col[2]);
                Media doc = Media.builder().title(col[0]).rate(rate).year(col[1]).genre(col[4]).build();
                br.operations(op -> op.index(idx -> idx.index(INDEX).document(doc)));
            }
            client.bulk(br.build());
        }
    }

    @Override
    public Page<Media> searchByTitle(String query, Pageable pageRequest) {
        SearchResponse<Media> search;
        int offset = pageRequest.getPageNumber() * pageRequest.getPageSize();
        try {
            search = client.search(s -> s.index(INDEX).query(q -> q.match(t -> t.field(TITLE_FIELD).query(query)))
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
        try {
            SearchResponse<Void> response = client.search(s -> s.index(INDEX).size(0)
                    .query(q -> q.matchAll(ma -> ma))
                    .aggregations("rate-ranges-count", a -> a.range( r -> r.field(RATE_FIELD)
                            .ranges(rs -> rs.key(RateRangeOption.ABOVE
                                    .getRepresentation()).from("8"))
                            .ranges(rs -> rs.key(RateRangeOption.BETWEEN6AND8
                                    .getRepresentation()).from("6").to("8"))
                            .ranges(rs -> rs.key(RateRangeOption
                                    .BETWEEN4AND6.getRepresentation()).from("4").to("6"))
                            .ranges(rs -> rs.key(RateRangeOption
                                    .BETWEEN2AND4.getRepresentation()).from("2").to("4"))
                            .ranges(rs -> rs.key(RateRangeOption
                                    // The .from(0) method is added to exclude those documents that originally had
                                    // no rate.
                                    .BELLOW.getRepresentation()).to("2").from("0")))),
                    Void.class);
            List<RangeBucket> buckets = response.aggregations().get("rate-ranges-count").range().buckets().array();
            HashMap<String, Integer> results = new HashMap<>();
            buckets.forEach(bucket -> results.put(bucket.key(), (int) bucket.docCount()));
            return results;
        } catch (IOException | NullPointerException ex) {
            throw new IllegalStateException(ex);
        }

    }

    @Override
    public Page<Media> searchByTitleInRateRange(String genre, Integer rangeCode, Pageable pageRequest) {
        RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field(RATE_FIELD);
        RateRangeOption rateRangeOption = RateRangeOption.retrieveByCode(rangeCode);
        if (rateRangeOption == null){
            throw new IllegalArgumentException("The given rate range code is invalid, valid codes are: "
                    + RateRangeOption.VALID_CODES.toString());
        }
        switch (rateRangeOption) {
            case ABOVE -> rangeQueryBuilder.gte(JsonData.of(8));
            case BETWEEN6AND8 -> rangeQueryBuilder.gte(JsonData.of(6)).lt(JsonData.of(8));
            case BETWEEN4AND6 -> rangeQueryBuilder.gte(JsonData.of(4)).lt(JsonData.of(6));
            case BETWEEN2AND4 -> rangeQueryBuilder.gte(JsonData.of(2)).lt(JsonData.of(4));
            // The .from(0) method is added to exclude those documents that originally had
            // no rate.
            case BELLOW -> rangeQueryBuilder.lt(JsonData.of(2)).gte(JsonData.of(0));
            default -> {
            }
        }
        Query byRateRange = RangeQuery.of(r -> rangeQueryBuilder)._toQuery();
        Query byGenre = MatchQuery.of(t -> t.field(GENRE_FIELD).fuzziness("0").query(genre))._toQuery();
        int offset = pageRequest.getPageNumber() * pageRequest.getPageSize();
        try {
            SearchResponse<Media> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(byGenre)
                                            .must(byRateRange)
                                    )
                            ).from(offset).size(pageRequest.getPageSize()),
                    Media.class
            );
            List<Media> results = new ArrayList<>();
            response.hits().hits().forEach(e -> results.add(e.source()));
            return new PageImpl<>(results, pageRequest, response.hits().total().value());
        } catch (IOException | NullPointerException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

