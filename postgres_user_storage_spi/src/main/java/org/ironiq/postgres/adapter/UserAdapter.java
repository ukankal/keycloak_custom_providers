package org.ironiq.postgres.adapter;

import org.ironiq.postgres.adapter.*;
import org.ironiq.postgres.daoImpl.*;
import org.ironiq.postgres.models.*;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.ClientModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Set;
import java.util.HashSet;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {

  private final UserEntity user;
  private final String keycloakId;
  private final UserDaoImpl userDaoImpl;
  private final RoleDaoImpl roleDaoImpl;
  private final GroupDaoImpl groupDaoImpl;
  protected KeycloakSession session;
  protected ComponentModel model;
  protected RealmModel realm;


  public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model,
      UserEntity user, UserDaoImpl userDaoImpl, RoleDaoImpl roleDaoImpl,
      GroupDaoImpl groupDaoImpl) {
    super(session, realm, model);
    this.user = user;
    this.session = session;
    this.realm = realm;
    this.model = model;
    this.keycloakId = StorageId.keycloakId(model, user.getId());
    this.userDaoImpl = userDaoImpl;
    this.roleDaoImpl = roleDaoImpl;
    this.groupDaoImpl = groupDaoImpl;
  }

  @Override
  public void addRequiredAction(String action) {
    userDaoImpl.addRequiredAction(user.getId(), action);
  }

  @Override
  public void addRequiredAction(UserModel.RequiredAction action) {
    userDaoImpl.addRequiredAction(user.getId(), action);
  }

  @Override
  protected boolean appendDefaultGroups() {
    return true;
  }

  @Override
  protected boolean appendDefaultRolesToRoleMappings() {
    return true;
  }

  @Override
  public void deleteRoleMapping(RoleModel role) {
    roleDaoImpl.revokeRoleToUser(user.getId(), role.getId());
  }


  @Override
  public Set<RoleModel> getClientRoleMappings(ClientModel app) {
    Set<RoleModel> clientRoles = new HashSet<RoleModel>();
    Set<Role> roles = roleDaoImpl.getClientRoleMappings(app.getClientId());
    roles.forEach((role) -> {
      clientRoles.add(new RoleAdapter(session, realm, model, role, roleDaoImpl));
    });
    return clientRoles;
  }

  public Set<GroupModel> getGroups() {
    Set<GroupModel> groupModels = new HashSet<GroupModel>();
    Set<Group> groups = groupDaoImpl.getGroups(user.getId());
    groups.forEach((group) -> {
      groupModels.add(new GroupAdapter(session, realm, model, group, groupDaoImpl));
    });
    return groupModels;
  }

  protected Set<GroupModel> getGroupsInternal() {
    Set<GroupModel> groupModels = new HashSet<GroupModel>();
    Set<Group> groups = groupDaoImpl.getGroups(user.getId());
    groups.forEach((group) -> {
      groupModels.add(new GroupAdapter(session, realm, model, group, groupDaoImpl));
    });
    return groupModels;
  }

  @Override
  public String getId() {
    return keycloakId;
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public void setUsername(String username) {
    user.setUsername(username);
    userDaoImpl.updateUser(user);
  }

  @Override
  public String getEmail() {
    return user.getEmail();
  }


  @Override
  public void setEmail(String email) {
    user.setEmail(email);
    userDaoImpl.updateUser(user);
  }

  @Override
  public String getFirstName() {
    return user.getFirstName();
  }

  @Override
  public void setFirstName(String firstName) {
    user.setFirstName(firstName);
    userDaoImpl.updateUser(user);
  }

  @Override
  public String getLastName() {
    return user.getLastName();
  }

  @Override
  public void setLastName(String lastName) {
    user.setLastName(lastName);
    userDaoImpl.updateUser(user);
  }

  // TODO: handle string to long conversion
  @Override
  public Long getCreatedTimestamp() {
    return null;
    // return user.getCreatedTimestamp();
  }

  // TODO: handle long to timestamp conversion for arg timestamp
  @Override
  public void setCreatedTimestamp(Long timestamp) {
    // user.setCreatedTimestamp(timestamp);
    userDaoImpl.updateUser(user);
  }

  @Override
  public void setEmailVerified(boolean verified) {
    user.setEmailVerified(verified);
    userDaoImpl.updateUser(user);
  }

  @Override
  public void setEnabled(boolean enabled) {
    user.setEnabled(enabled);
    userDaoImpl.updateUser(user);
  }

  @Override
  public boolean isEmailVerified() {
    return user.getEmailVerified();
  }

  @Override
  public boolean isEnabled() {
    return user.getEnabled();
  }

  @Override
  public Set<RoleModel> getRealmRoleMappings() {
    Set<RoleModel> realmRoles = new HashSet<RoleModel>();
    Set<Role> roles = roleDaoImpl.getRoleMappings(user.getId());
    roles.forEach((role) -> {
      realmRoles.add(new RoleAdapter(session, realm, model, role, roleDaoImpl));
    });
    return realmRoles;
  }

  @Override
  public Set<String> getRequiredActions() {
    return userDaoImpl.getRequiredActions(user.getId());
  }

  @Override
  public Set<RoleModel> getRoleMappings() {
    Set<RoleModel> realmRoles = new HashSet<RoleModel>();
    Set<Role> roles = roleDaoImpl.getRoleMappings(user.getId());
    roles.forEach((role) -> {
      realmRoles.add(new RoleAdapter(session, realm, model, role, roleDaoImpl));
    });
    return realmRoles;
  }

  @Override
  protected Set<RoleModel> getRoleMappingsInternal() {
    Set<RoleModel> realmRoles = new HashSet<RoleModel>();
    Set<Role> roles = roleDaoImpl.getRoleMappings(user.getId());
    roles.forEach((role) -> {
      realmRoles.add(new RoleAdapter(session, realm, model, role, roleDaoImpl));
    });
    return realmRoles;
  }

  // public void grantRole(RoleModel role) {

  // }

  // public int hashCode() {
  // return 2;
  // }

  // public boolean hasRole(RoleModel role) {
  // return true;
  // }


  // public boolean isMemberOf(GroupModel group) {
  // return true;
  // }


  // public void joinGroup(GroupModel group) {
  // }


  // public void leaveGroup(GroupModel group) {
  // }


  public void removeRequiredAction(String action) {
    userDaoImpl.removeRequiredAction(user.getId(), action);
  }


  public void removeRequiredAction(UserModel.RequiredAction action) {
    userDaoImpl.removeRequiredAction(user.getId(), action);
  }
}
