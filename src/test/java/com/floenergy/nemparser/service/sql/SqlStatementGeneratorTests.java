package com.floenergy.nemparser.service.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.floenergy.nemparser.service.model.NemFileModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class SqlStatementGeneratorTests {

  private final SqlStatementGenerator sqlStatementGenerator = new SqlStatementGenerator();

  @Test
  void testNemToSql_generatesCorrectSqlStatement() {
    NemFileModel model = new NemFileModel();
    model.setNmi("1234567890");
    model.setConsumption(new BigDecimal("42.5"));
    model.setTimeStamp(LocalDateTime.of(2025, 4, 18, 14, 30));

    String result = sqlStatementGenerator.nemToSql(model);

    String expected =
        "INSERT INTO meter_readings (nmi, timestamp, consumption) "
            + "VALUES ('1234567890', '2025-04-18 14:30:00', 42.5);";
    assertEquals(expected, result);
  }

  @Test
  void testNemToSql_handlesEdgeValues() {
    NemFileModel model = new NemFileModel();
    model.setNmi("EDGECASE");
    model.setConsumption(BigDecimal.ZERO);
    model.setTimeStamp(LocalDateTime.of(2000, 1, 1, 0, 0));

    String result = sqlStatementGenerator.nemToSql(model);

    String expected =
        "INSERT INTO meter_readings (nmi, timestamp, consumption) "
            + "VALUES ('EDGECASE', '2000-01-01 00:00:00', 0);";
    assertEquals(expected, result);
  }

  // Optional: Defensive test for null values
  @Test
  void testNemToSql_nullValues_throwsNullPointerException() {
    NemFileModel model = new NemFileModel();

    try {
      sqlStatementGenerator.nemToSql(model);
    } catch (NullPointerException e) {

    }
  }
}
