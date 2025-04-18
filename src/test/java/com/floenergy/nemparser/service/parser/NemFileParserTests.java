package com.floenergy.nemparser.service.parser;

import static org.mockito.Mockito.*;

import com.floenergy.nemparser.service.model.NemFileModel;
import com.floenergy.nemparser.service.sql.SqlStatementGenerator;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

public class NemFileParserTests {
  private SqlStatementGenerator sqlGen;
  private NemFileParser parser;

  @BeforeEach
  void setUp() {
    sqlGen = mock(SqlStatementGenerator.class);
    parser = new NemFileParser(sqlGen);
  }

  @Test
  void testParseNemFileToSql_exampleData(@TempDir Path tempDir) throws IOException {
    String content =
        "100,NEM12,200506081149,UNITEDDP,NEMMCO\n"
            + "200,NEM1201009,E1E2,1,E1,N1,01009,kWh,30,20050610\n"
            + "300,20050301,0,0,0,0,0,0,0,0,0,0,0,0,0.461,0.810,0.568,1.234,1.353,1.507,1.344,1.773,0.848,\n"
            + "1.\n"
            + "271,0.895,1.327,1.013,1.793,0.988,0.985,0.876,0.555,0.760,0.938,0.566,0.512,0.970,0.760,0.7\n"
            + "31,0.615,0.886,0.531,0.774,0.712,0.598,0.670,0.587,0.657,0.345,0.231,A,,,20050310121004,2\n"
            + "0050310182204\n"
            + "300,20050302,0,0,0,0,0,0,0,0,0,0,0,0,0.235,0.567,0.890,1.123,1.345,1.567,1.543,1.234,0.987,\n"
            + "1.\n"
            + "123,0.876,1.345,1.145,1.173,1.265,0.987,0.678,0.998,0.768,0.954,0.876,0.845,0.932,0.786,0.9\n"
            + "99,0.879,0.777,0.578,0.709,0.772,0.625,0.653,0.543,0.599,0.432,0.432,A,,,20050310121004,2\n"
            + "0050310182204\n"
            + "300,20050303,0,0,0,0,0,0,0,0,0,0,0,0,0.261,0.310,0.678,0.934,1.211,1.134,1.423,1.370,0.988,\n"
            + "1.\n"
            + "207,0.890,1.320,1.130,1.913,1.180,0.950,0.746,0.635,0.956,0.887,0.560,0.700,0.788,0.668,0.5\n"
            + "43,0.738,0.802,0.490,0.598,0.809,0.520,0.670,0.570,0.600,0.289,0.321,A,,,20050310121004,2\n"
            + "0050310182204\n"
            + "300,20050304,0,0,0,0,0,0,0,0,0,0,0,0,0.335,0.667,0.790,1.023,1.145,1.777,1.563,1.344,1.087,\n"
            + "1.\n"
            + "453,0.996,1.125,1.435,1.263,1.085,1.487,1.278,0.768,0.878,0.754,0.476,1.045,1.132,0.896,0.8\n"
            + "79,0.679,0.887,0.784,0.954,0.712,0.599,0.593,0.674,0.799,0.232,0.612,A,,,20050310121004,2\n"
            + "0050310182204\n"
            + "500,O,S01009,20050310121004,\n"
            + "200,NEM1201010,E1E2,2,E2,,01009,kWh,30,20050610\n"
            + "300,20050301,0,0,0,0,0,0,0,0,0,0,0,0,0.154,0.460,0.770,1.003,1.059,1.750,1.423,1.200,0.980,\n"
            + "1.\n"
            + "111,0.800,1.403,1.145,1.173,1.065,1.187,0.900,0.998,0.768,1.432,0.899,1.211,0.873,0.786,1.5\n"
            + "04,0.719,0.817,0.780,0.709,0.700,0.565,0.655,0.543,0.786,0.430,0.432,A,,,20050310121004,\n"
            + "300,20050302,0,0,0,0,0,0,0,0,0,0,0,0,0.461,0.810,0.776,1.004,1.034,1.200,1.310,1.342,0.998,\n"
            + "1.\n"
            + "311,1.095,1.320,1.115,1.436,0.890,1.255,0.916,0.955,0.711,0.780,0.606,0.510,0.905,0.660,0.8\n"
            + "35,0.798,0.965,1.122,1.004,0.772,0.508,0.670,0.670,0.432,0.415,0.220,A,,,20050310121004,\n"
            + "300,20050303,0,0,0,0,0,0,0,0,0,0,0,0,0.335,0.667,0.790,1.023,1.145,1.777,1.563,1.344,1.087,\n"
            + "1.\n"
            + "453,0.996,1.125,1.435,1.263,1.085,1.487,1.278,0.768,0.878,0.754,0.476,1.045,1.132,0.896,0.8\n"
            + "79,0.679,0.887,0.784,0.954,0.712,0.599,0.593,0.674,0.799,0.232,0.610,A,,,20050310121004,\n"
            + "300,20050304,0,0,0,0,0,0,0,0,0,0,0,0,0.461,0.415,0.778,0.940,1.191,1.345,1.390,1.222,1.134,\n"
            + "1.\n"
            + "207,0.877,1.655,1.099,1.625,1.010,0.950,1.255,0.635,0.956,0.880,0.660,0.810,0.878,0.778,0.6\n"
            + "43,0.838,0.812,0.490,0.598,0.811,0.572,0.417,0.707,0.670,0.290,0.355,A,,,20050310121004,\n"
            + "500,O,S01009,20050310121004,\n"
            + "900";
    Path file = tempDir.resolve("example.nem12");
    Files.writeString(file, content);

    parser.parseNemFileToSql(file);
    ArgumentCaptor<NemFileModel> captor = ArgumentCaptor.forClass(NemFileModel.class);
    verify(sqlGen, times(168)).nemToSql(captor.capture());
  }

  @Test
  void testParseNemFileToSql_multipleDays(@TempDir Path tempDir) throws IOException {
    // Two days of readings under the same NMI with interval length of 15 minutes
    String content =
        "200,NEM1201009,_,_,_,_,_,kWh,15,20250102\n"
            + "300,20250102,0.1,0.2\n"
            + "300,20250103,0.3,0.4\n";
    Path file = tempDir.resolve("test2.nem12");
    Files.writeString(file, content);

    parser.parseNemFileToSql(file);

    ArgumentCaptor<NemFileModel> captor = ArgumentCaptor.forClass(NemFileModel.class);
    verify(sqlGen, times(4)).nemToSql(captor.capture());

    List<NemFileModel> calls = captor.getAllValues();

    assert calls.size() == 4;

    assert calls.get(0).nmi.equals("NEM1201009");
    assert calls.get(0).consumption.compareTo(new BigDecimal("0.1")) == 0;
    assert calls.get(0).timeStamp.equals(LocalDateTime.of(2025, 1, 2, 0, 0));

    assert calls.get(1).nmi.equals("NEM1201009");
    assert calls.get(1).consumption.compareTo(new BigDecimal("0.2")) == 0;
    assert calls.get(1).timeStamp.equals(LocalDateTime.of(2025, 1, 2, 0, 15));

    assert calls.get(2).nmi.equals("NEM1201009");
    assert calls.get(2).consumption.compareTo(new BigDecimal("0.3")) == 0;
    assert calls.get(2).timeStamp.equals(LocalDateTime.of(2025, 1, 3, 0, 0));

    assert calls.get(3).nmi.equals("NEM1201009");
    assert calls.get(3).consumption.compareTo(new BigDecimal("0.4")) == 0;
    assert calls.get(3).timeStamp.equals(LocalDateTime.of(2025, 1, 3, 0, 15));
  }

  @Test
  void testParseNemFileToSql_multipleNmi(@TempDir Path tempDir) throws IOException {
    // Two days of readings under the same NMI with interval length of 15 minutes
    String content =
        "200,NEM1201009,E1E2,1,E1,N1,01009,kWh,15,20050610\n"
            + "300,20250102,0.1,0.2\n"
            + "300,20250103,0.3,0.4\n"
            + "200,NEW_NEM,E1E2,1,E1,N1,01009,kWh,30,20050610\n"
            + "300,20250105,1.1,1.2\n";
    Path file = tempDir.resolve("test2.nem12");
    Files.writeString(file, content);

    parser.parseNemFileToSql(file);

    ArgumentCaptor<NemFileModel> captor = ArgumentCaptor.forClass(NemFileModel.class);
    verify(sqlGen, times(6)).nemToSql(captor.capture());

    List<NemFileModel> calls = captor.getAllValues();

    assert calls.size() == 6;

    assert calls.get(0).nmi.equals("NEM1201009");
    assert calls.get(0).consumption.compareTo(new BigDecimal("0.1")) == 0;
    assert calls.get(0).timeStamp.equals(LocalDateTime.of(2025, 1, 2, 0, 0));

    assert calls.get(1).nmi.equals("NEM1201009");
    assert calls.get(1).consumption.compareTo(new BigDecimal("0.2")) == 0;
    assert calls.get(1).timeStamp.equals(LocalDateTime.of(2025, 1, 2, 0, 15));

    assert calls.get(2).nmi.equals("NEM1201009");
    assert calls.get(2).consumption.compareTo(new BigDecimal("0.3")) == 0;
    assert calls.get(2).timeStamp.equals(LocalDateTime.of(2025, 1, 3, 0, 0));

    assert calls.get(3).nmi.equals("NEM1201009");
    assert calls.get(3).consumption.compareTo(new BigDecimal("0.4")) == 0;
    assert calls.get(3).timeStamp.equals(LocalDateTime.of(2025, 1, 3, 0, 15));

    assert calls.get(4).nmi.equals("NEW_NEM");
    assert calls.get(4).consumption.compareTo(new BigDecimal("1.1")) == 0;
    assert calls.get(4).timeStamp.equals(LocalDateTime.of(2025, 1, 5, 0, 0));

    assert calls.get(5).nmi.equals("NEW_NEM");
    assert calls.get(5).consumption.compareTo(new BigDecimal("1.2")) == 0;
    assert calls.get(5).timeStamp.equals(LocalDateTime.of(2025, 1, 5, 0, 30));
  }

  @Test
  void testParseNemFileToSql_300_before_200(@TempDir Path tempDir) throws IOException {
    String content = "300,20250105,9.99";
    Path file = tempDir.resolve("test3.nem12");
    Files.writeString(file, content);

    parser.parseNemFileToSql(file);

    verifyNoInteractions(sqlGen);
  }
}
