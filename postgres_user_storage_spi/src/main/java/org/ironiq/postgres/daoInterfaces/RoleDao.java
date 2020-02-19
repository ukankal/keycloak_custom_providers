package org.ironiq.postgres.daoInterfaces;

import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.models.Role;
import java.util.List;
import java.util.Set;

public interface RoleDao {

  List<UserEntity> getRoleMembers(String roleId, int offset, int limit);

  Set<Role> getRealmRoleMappingsForUser(String userId);

  Set<Role> getClientRoleMappingsForUser(String userId, String clientId);

  Set<Role> getRoleMappingsForUser(String userId);

  boolean grantRoleToUser(String username, String roleId);

  boolean revokeRoleToUser(String username, String roleId);

  boolean hasRole(String username, String roleId);

  Set<Role> getRealmRoleMappingsForGroup(String groupId);

  Set<Role> getClientRoleMappingsForGroup(String groupId, String clientId);

  Set<Role> getRoleMappingsForGroup(String groupId);

  boolean isClientRole(String roleId);

  String getClientName(String roleId);
}
