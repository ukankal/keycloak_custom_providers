package org.ironiq.postgres.adapter;

import org.ironiq.postgres.daoImpl.*;
import org.ironiq.postgres.models.*;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.ClientModel;
import org.keycloak.storage.StorageId;

import java.util.List;
import java.util.Set;
import java.util.Map;

public class GroupAdapter implements GroupModel {

  private final Group group;
  private final GroupDaoImpl groupDaoImpl;

  public GroupAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Group group,
      GroupDaoImpl groupDaoImpl) {
    this.group = group;
    this.groupDaoImpl = groupDaoImpl;
  }

  class GroupRemovedEventImpl implements GroupModel.GroupRemovedEvent {
    public RealmModel getRealm() {
      return null;
    }

    public GroupModel getGroup() {
      return null;
    }

    public KeycloakSession getKeycloakSession() {
      return null;
    }
  }


  public String getId() {
    return group.getId();
  }


  public String getName() {
    return group.getName();
  }

  public void setName(String name) {
    group.setName(name);
    groupDaoImpl.updateGroup(group);
  }

  public void setSingleAttribute(String name, String value) {
    return;
  }

  public void setAttribute(String name, List<String> values) {
    return;
  }

  public void removeAttribute(String name) {
    return;
  }

  public String getFirstAttribute(String name) {
    return null;
  }

  public List<String> getAttribute(String name) {
    return null;
  }

  public Map<String, List<String>> getAttributes() {
    return null;
  }

  public GroupModel getParent() {
    return null;
  }

  public String getParentId() {
    return null;
  }

  public Set<GroupModel> getSubGroups() {
    return null;
  }

  public void setParent(GroupModel group) {
    return;
  }

  public void addChild(GroupModel subGroup) {
    return;
  }

  public void removeChild(GroupModel subGroup) {
    return;
  }

  public Set<RoleModel> getRealmRoleMappings() {
    return null;
  }

  public Set<RoleModel> getClientRoleMappings(ClientModel app) {
    return null;
  }

  public boolean hasRole(RoleModel role) {
    return false;
  }

  public void grantRole(RoleModel role) {
    return;
  }

  public Set<RoleModel> getRoleMappings() {
    return null;
  }

  public void deleteRoleMapping(RoleModel role) {
    return;
  }

}
