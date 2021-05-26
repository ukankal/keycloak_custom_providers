package org.ironiq.postgres.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
  private String id;
  private String email;
  private Boolean emailVerified;
  private Boolean enabled;
  private String firstName;
  private String lastName;
  private String username;
  private String createdTimestamp;
}
