package org.ironiq.postgres.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
  private String id;
  private Boolean clientRole;
  private String description;
  private String name;
  private String clientId;
  private Boolean defaultRole;
}
