package com.floenergy.nemparser.service.sql;

import com.floenergy.nemparser.service.model.NemFileModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class SqlStatementGenerator {

  private static final DateTimeFormatter SQL_TIMESTAMP_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public String nemToSql(NemFileModel nemFileModel) {
    return String.format(
        "INSERT INTO meter_readings (nmi, timestamp, consumption) " + "VALUES ('%s', '%s', %s);",
        nemFileModel.nmi, timeStampToSqlDate(nemFileModel.timeStamp), nemFileModel.consumption);
  }

  private String timeStampToSqlDate(LocalDateTime localDateTime) {
    return localDateTime.format(SQL_TIMESTAMP_FORMAT);
  }
}
