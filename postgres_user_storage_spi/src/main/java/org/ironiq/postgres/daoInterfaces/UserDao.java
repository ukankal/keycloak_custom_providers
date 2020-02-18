package org.ironiq.postgres.daoInterfaces;

import org.keycloak.models.UserModel;
import org.ironiq.postgres.models.UserEntity;
import java.util.List;
import java.util.Set;
import java.util.Map;

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

  List<UserEntity> searchForUser(String searchTerm);

  List<UserEntity> searchForUser(String searchTerm, int start, int max);

  List<UserEntity> searchForUser(Map<String, String> params);

  List<UserEntity> searchForUser(Map<String, String> params, int start, int max);
}
