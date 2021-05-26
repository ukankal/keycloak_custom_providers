package org.ironiq.postgres.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credential {
  private String id;
  private String device;
  private int hashIterations;
  private String type;
  private String value;
  private String userId;
  private String createdDate;
  private int counter;
  private int digits;
  private int period;
  private String algorithm;
}
