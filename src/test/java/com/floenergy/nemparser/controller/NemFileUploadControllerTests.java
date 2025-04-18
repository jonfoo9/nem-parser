package com.floenergy.nemparser.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.floenergy.nemparser.service.NemFileProcessingService;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class NemFileUploadControllerTests {

  private MockMvc mockMvc;

  @Mock private NemFileProcessingService nemFileProcessingService;

  @InjectMocks private NemFileUploadController nemFileUploadController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(nemFileUploadController).build();
  }

  @Test
  void testUploadNemFile_success() throws Exception {
    String content = "some,csv,content";
    MockMultipartFile file = new MockMultipartFile("file", "nem", "text/csv", content.getBytes());

    when(nemFileProcessingService.processFile(any())).thenReturn(Arrays.asList("SQL 1", "SQL 2"));

    mockMvc
        .perform(
            multipart("/api/process_nem").file(file).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[\"SQL 1\", \"SQL 2\"]"));
  }

  @Test
  void testUploadNemFile_internalServerError() throws Exception {
    String content = "invalid";
    MockMultipartFile file =
        new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());

    when(nemFileProcessingService.processFile(any()))
        .thenThrow(new RuntimeException("Unexpected error"));

    mockMvc
        .perform(
            multipart("/api/process_nem").file(file).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    "{\"error\":\"Unexpected error occurred\", \"message\":\"Unexpected error\"}"));
  }

  @Test
  void testUploadNemFile_emptyFile() throws Exception {
    // Prepare an empty mock MultipartFile
    MockMultipartFile emptyFile =
        new MockMultipartFile("file", "empty.csv", "text/csv", "".getBytes());

    // Mock the service response (an empty file results in an empty list)
    when(nemFileProcessingService.processFile(any())).thenReturn(Arrays.asList());

    // Perform the request and verify the response
    mockMvc
        .perform(
            multipart("/api/process_nem")
                .file(emptyFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }
}
