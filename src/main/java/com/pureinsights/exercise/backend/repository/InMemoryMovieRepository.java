package com.pureinsights.exercise.backend.repository;

import com.opencsv.CSVReader;
import com.pureinsights.exercise.backend.model.Movie;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In-memory implementation of {@link MovieRepository}
 * @author Andres Marenco
 */
@Repository
@Slf4j
public class InMemoryMovieRepository implements MovieRepository, Closeable {
  /** Input file with the movie collection */
  private static final String MOVIE_COLLECTION = "in-memory-movies.csv";
  /** Field with the name of the movie */
  private static final String NAME_FIELD = "name";
  /** Field with the rating */
  private static final String YEAR_FIELD = "year";

  /** Default analyzer for query/index */
  private static final Analyzer DEFAULT_ANALYZER = new StandardAnalyzer();
  /** Query parser for the name field */ 
  private static final QueryParser QUERY_PARSER = new QueryParser(NAME_FIELD, DEFAULT_ANALYZER);

  /** In-memory Lucene index */
  protected final Directory movieIndex = new ByteBuffersDirectory();
  /** Reader for the movie collection */
  protected DirectoryReader indexReader;
  /** Searcher for the movie collection */
  protected IndexSearcher indexSearcher;


  @PostConstruct
  void init() throws IOException {
    log.info("Initializing in-memory index with collection: {}", MOVIE_COLLECTION);
    try (var indexWriter = new IndexWriter(movieIndex, new IndexWriterConfig(DEFAULT_ANALYZER));
         var dataReader = new CSVReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(MOVIE_COLLECTION))))) {

      dataReader.skip(1);
      for (var col : dataReader) {
        var doc = new Document();
        doc.add(new TextField(NAME_FIELD, col[0], Field.Store.YES));
        doc.add(new StoredField(YEAR_FIELD, Integer.parseInt(col[1])));

        indexWriter.addDocument(doc);
      }
    }

    this.indexReader = DirectoryReader.open(movieIndex);
    this.indexSearcher = new IndexSearcher(this.indexReader);

    log.info("In-memory index successfully initialized");
  }


  @Override
  public Page<Movie> search(String query, Pageable pageRequest) {
    try {
      var parsedQuery = QUERY_PARSER.parse(query);
      var count = indexSearcher.count(parsedQuery);
      var offset = pageRequest.getPageNumber() * pageRequest.getPageSize();
      var search = indexSearcher.search(parsedQuery, offset + pageRequest.getPageSize());
      var movies = Stream.of(search.scoreDocs)
          .skip(offset)
          .limit(pageRequest.getPageSize())
          .map(this::loadDocument)
          .map(doc -> Movie.builder().name(doc.get(NAME_FIELD)).year(doc.getField(YEAR_FIELD).numericValue().intValue()).build())
          .collect(Collectors.toList());

      return new PageImpl<>(movies, pageRequest, count);
    } catch (ParseException ex) {
      throw new IllegalArgumentException(ex);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }


  /**
   * @param scoreDoc the scored document of the document to load
   * @return the indexed document
   */
  private Document loadDocument(ScoreDoc scoreDoc) {
    try {
      return indexReader.document(scoreDoc.doc);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }


  @Override
  public void close() throws IOException {
    if (indexReader != null) {
      indexReader.close();
    }

    movieIndex.close();
  }
}
