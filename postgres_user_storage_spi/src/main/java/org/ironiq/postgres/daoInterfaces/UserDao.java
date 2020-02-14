package org.ironiq.postgres.daoInterfaces;

import org.keycloak.models.UserModel;
import org.ironiq.postgres.models.UserEntity;
import java.util.List;
import java.util.Set;

public interface UserDao {

  UserEntity getUserById(String id);

  UserEntity getUserByUsername(String username);

  UserEntity getUserByEmail(String email);

  int getUsersCount();

  List<UserEntity> getAllUsers();

  List<UserEntity> getAllUsers(int start, int max);

  boolean insertUser(UserEntity user);

  boolean updateUser(UserEntity user);

  boolean deleteUser(UserEntity user);

  void addRequiredAction(String userId, String action);

  void addRequiredAction(String userId, UserModel.RequiredAction action);

  Set<String> getRequiredActions(String userId);

  void removeRequiredAction(String userId, String action);

  void removeRequiredAction(String userId, UserModel.RequiredAction action);
}
