package org.ironiq.postgres.daoInterfaces;

import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.models.Group$;
import java.util.List;
import java.util.Set;

public interface GroupDao {

  boolean updateGroup(Group$ group);

  List<UserEntity> getGroupMembers(String groupId, int firstResult, int maxResults);

  List<UserEntity> getGroupMembers(String groupId);

  Set<Group$> getGroups(String userId);

  boolean isUserMemberOf(String userId, String groupId);

  boolean addUserToGroup(String userId, String groupId);

  boolean removeUserFromGroup(String userId, String groupId);

  Group$ getParent(String groupId);

  Set<Group$> getSubGroups(String groupId);
}
