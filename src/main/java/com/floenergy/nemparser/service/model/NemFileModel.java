package com.floenergy.nemparser.service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NemFileModel {
  public String nmi;
  public BigDecimal consumption;
  public LocalDateTime timeStamp;
}
