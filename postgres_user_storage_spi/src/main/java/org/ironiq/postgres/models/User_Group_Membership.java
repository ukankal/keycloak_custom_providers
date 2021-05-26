package org.ironiq.postgres.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_Group_Membership {
  private String userId;
  private String groupId;
}
