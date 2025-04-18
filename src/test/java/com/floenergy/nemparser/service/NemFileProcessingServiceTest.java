package com.floenergy.nemparser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.floenergy.nemparser.service.parser.NemFileParser;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

class NemFileProcessingServiceTest {

  @Mock private NemFileParser nemFileParser;

  @InjectMocks private NemFileProcessingService nemFileProcessingService;

  private MockMultipartFile mockFile;

  @BeforeEach
  void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);

    String content = "some,csv,content";
    mockFile = new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());
  }

  @Test
  void testProcessFile_successfulProcessing() throws IOException {
    List<String> mockSqlStatements = Arrays.asList("SQL Statement 1", "SQL Statement 2");
    when(nemFileParser.parseNemFileToSql(any(Path.class))).thenReturn(mockSqlStatements);

    List<String> sqlStatements = nemFileProcessingService.processFile(mockFile);

    assertNotNull(sqlStatements);
    assertEquals(2, sqlStatements.size());
    assertEquals("SQL Statement 1", sqlStatements.get(0));
    assertEquals("SQL Statement 2", sqlStatements.get(1));

    Mockito.verify(nemFileParser, Mockito.times(1)).parseNemFileToSql(any(Path.class));
  }

  @Test
  void testProcessFile_parsingFails() throws IOException {
    when(nemFileParser.parseNemFileToSql(any(Path.class)))
        .thenThrow(new RuntimeException("Parsing error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              nemFileProcessingService.processFile(mockFile);
            });

    assertEquals("Parsing error", exception.getMessage());
  }

  @Test
  void testProcessFile_emptyFile() throws IOException {
    MockMultipartFile emptyFile =
        new MockMultipartFile("file", "empty.csv", "text/csv", "".getBytes());

    List<String> mockSqlStatements = Arrays.asList();
    when(nemFileParser.parseNemFileToSql(any(Path.class))).thenReturn(mockSqlStatements);

    List<String> sqlStatements = nemFileProcessingService.processFile(emptyFile);

    assertNotNull(sqlStatements);
    assertTrue(sqlStatements.isEmpty());
  }
}
