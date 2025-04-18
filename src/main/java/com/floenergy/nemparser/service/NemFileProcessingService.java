package com.floenergy.nemparser.service;

import com.floenergy.nemparser.service.parser.NemFileParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class NemFileProcessingService {

  private final NemFileParser nemFileParser;

  @Autowired
  public NemFileProcessingService(NemFileParser nemFileParser) {
    this.nemFileParser = nemFileParser;
  }

  public List<String> processFile(MultipartFile file) {
    try {
      Path nemFilePath = Files.createTempFile("nem12", ".csv");
      file.transferTo(nemFilePath.toFile());
      List<String> sqlStatements = nemFileParser.parseNemFileToSql(nemFilePath);
      return sqlStatements;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
