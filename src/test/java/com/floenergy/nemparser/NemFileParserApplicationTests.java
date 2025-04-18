package com.floenergy.nemparser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class NemFileParserApplicationTests {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testUploadNemFile_returnsExpected() throws Exception {
    ClassPathResource nemFileResource = new ClassPathResource("nem12_file");
    ClassPathResource expectedJsonPath = new ClassPathResource("expectedSqlOut.json");
    String expectedJson =
        new String(expectedJsonPath.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", nemFileResource.getFilename(), "text/plain", nemFileResource.getInputStream());

    MvcResult result =
        mockMvc
            .perform(
                multipart("/api/process_nem").file(file).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

    String actualJson = result.getResponse().getContentAsString();

    assertThat(objectMapper.readTree(actualJson)).isEqualTo(objectMapper.readTree(expectedJson));
  }
}
