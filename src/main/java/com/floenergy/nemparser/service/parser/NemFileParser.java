package com.floenergy.nemparser.service.parser;

import com.floenergy.nemparser.service.model.NemFileModel;
import com.floenergy.nemparser.service.sql.SqlStatementGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NemFileParser {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final SqlStatementGenerator sqlStatementGenerator;

  public NemFileParser(SqlStatementGenerator sqlStatementGenerator) {
    this.sqlStatementGenerator = sqlStatementGenerator;
  }

  public List<String> parseNemFileToSql(Path nemFilePath) throws IOException {
    List<String> sqlStatements = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(nemFilePath)) {

      String line;
      String currentNmi = null;
      int intervalLength = 0;

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",", -1);
        if (parts.length == 0) continue;

        switch (parts[0]) {
          case "200":
            currentNmi = parts[1];
            intervalLength = Integer.parseInt(parts[8]);
            break;

          case "300":
            if (currentNmi == null) {
              log.error("Missing NMI while processing file: {}", nemFilePath.getFileName());
              break;
            }
            LocalDate date = LocalDate.parse(parts[1], DATE_FORMAT);

            for (int i = 2; i < parts.length; i++) {
              String valueStr = parts[i].trim();

              if (valueStr.isEmpty() || !valueStr.matches("\\d+(\\.\\d+)?")) {
                continue;
              }
              BigDecimal consumption = new BigDecimal(valueStr);
              LocalDateTime timestamp = calculateTimestampFromInterval(date, i, intervalLength);

              NemFileModel current = new NemFileModel();
              current.nmi = currentNmi;
              current.consumption = consumption;
              current.timeStamp = timestamp;

              String sql = sqlStatementGenerator.nemToSql(current);
              sqlStatements.add(sql);
            }
            break;

          default:
            break;
        }
      }
    }
    return sqlStatements;
  }

  private LocalDateTime calculateTimestampFromInterval(
      LocalDate date, int column, int intervalLength) {
    return date.atStartOfDay().plusMinutes((long) (column - 2) * intervalLength);
  }
}
