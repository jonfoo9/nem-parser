package com.floenergy.nemparser.controller;

import com.floenergy.nemparser.service.NemFileProcessingService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class NemFileUploadController {

  private final NemFileProcessingService nemFileProcessingService;

  @Autowired
  public NemFileUploadController(NemFileProcessingService nemFileProcessingService) {
    this.nemFileProcessingService = nemFileProcessingService;
  }

  @PostMapping("/api/process_nem")
  public ResponseEntity<?> uploadNemFile(@RequestParam("file") MultipartFile file) {
    try {
      List<String> sqlStatements = nemFileProcessingService.processFile(file);
      return ResponseEntity.ok(sqlStatements);
    } catch (CompletionException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error processing the file", "message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Unexpected error occurred", "message", e.getMessage()));
    }
  }
}
