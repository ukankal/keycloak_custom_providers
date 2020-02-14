package org.ironiq.postgres.daoInterfaces;

import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.models.Role;
import org.ironiq.postgres.models.Group;
import java.util.List;
import java.util.Set;

public interface RoleDao {

  List<UserEntity> getRoleMembers(String roleId, int offset, int limit);

  Set<Role> getClientRoleMappings(String clientId);

  Set<Role> getRoleMappings(String userId);

  boolean grantRoleToUser(String username, String roleId);

  boolean revokeRoleToUser(String username, String roleId);

  boolean hasRole(String username, String roleId);
}
