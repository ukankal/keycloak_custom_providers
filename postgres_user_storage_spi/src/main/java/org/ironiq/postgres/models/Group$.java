package org.ironiq.postgres.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group$ {
  private String id;
  private String name;
  private String parentGroup;
  private Boolean defaultGroup;
}
