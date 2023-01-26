package com.pureinsights.exercise.backend.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InMemoryMovieRepository}
 * @author Andres Marenco
 */
@ExtendWith(MockitoExtension.class)
class InMemoryMovieRepositoryTest {

  @InjectMocks
  private InMemoryMovieRepository movieRepository;

  @Mock
  private DirectoryReader indexReader;

  @Mock
  private IndexSearcher indexSearcher;


  /**
   * Tests the {@link InMemoryMovieRepository#init()} method
   */
  @Test
  void initTest() throws Exception {
    movieRepository.init();
    assertEquals(12, movieRepository.indexSearcher.count(new MatchAllDocsQuery()));
  }


  /**
   * Tests the {@link InMemoryMovieRepository#search(String, Pageable)} method
   */
  @Test
  void searchTest() throws Exception {
    var query = RandomStringUtils.randomAlphabetic(5);
    var pageRequest = PageRequest.of(1, 2);
    var topDocs = new TopDocs(mock(TotalHits.class), new ScoreDoc[] {
        new ScoreDoc(1, 1f),
        new ScoreDoc(2, 1f),
        new ScoreDoc(3, 1f),
        new ScoreDoc(4, 1f)
    });

    var docC = new Document();
    docC.add(new TextField("name", "movie", Field.Store.YES));
    docC.add(new StoredField("year", 1999));

    var docD = new Document();
    docD.add(new TextField("name", "other", Field.Store.YES));
    docD.add(new StoredField("year", 1950));

    when(indexSearcher.count(any(Query.class))).thenReturn(20);
    when(indexSearcher.search(any(Query.class), eq(4))).thenReturn(topDocs);
    when(indexReader.document(3)).thenReturn(docC);
    when(indexReader.document(4)).thenReturn(docD);

    var result = movieRepository.search(query, pageRequest);
    assertEquals(2, result.getSize());
    assertEquals(20, result.getTotalElements());

    var movieC = result.getContent().get(0);
    var movieD = result.getContent().get(1);
    assertEquals("movie", movieC.getName());
    assertEquals(1999, movieC.getYear());
    assertEquals("other", movieD.getName());
    assertEquals(1950, movieD.getYear());

    var countQueryCaptor = ArgumentCaptor.forClass(Query.class);
    var searchQueryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(indexSearcher).count(countQueryCaptor.capture());
    verify(indexSearcher).search(searchQueryCaptor.capture(), eq(4));
    verify(indexReader, never()).document(1);
    verify(indexReader, never()).document(2);
  }


  /**
   * Tests the {@link InMemoryMovieRepository#search(String, Pageable)} method
   * when an {@link IllegalArgumentException} is thrown
   */
  @Test
  void search_illegalArgumentException_Test() {
    var pageRequest = PageRequest.of(1, 2);
    assertThrows(IllegalArgumentException.class, () -> movieRepository.search("a/b", pageRequest));
  }


  /**
   * Tests the {@link InMemoryMovieRepository#search(String, Pageable)} method
   * when an {@link IllegalStateException} is thrown
   */
  @Test
  void search_illegalStateException_Test() throws Exception {
    var query = RandomStringUtils.randomAlphabetic(5);
    var pageRequest = PageRequest.of(1, 2);

    when(indexSearcher.count(any(Query.class))).thenThrow(IOException.class);
    assertThrows(IllegalStateException.class, () -> movieRepository.search(query, pageRequest));
  }


  /**
   * Tests the {@link InMemoryMovieRepository#close()} method
   */
  @Test
  void closeTest() throws Exception {
    movieRepository.close();
    verify(indexReader).close();

    var uninitialized = new InMemoryMovieRepository();
    assertDoesNotThrow(uninitialized::close);
  }
}
