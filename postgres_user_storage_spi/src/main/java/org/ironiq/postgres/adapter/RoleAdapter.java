package org.ironiq.postgres.adapter;

import org.ironiq.postgres.daoImpl.*;
import org.ironiq.postgres.models.*;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.storage.StorageId;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class RoleAdapter implements RoleModel {

  private final Role role;
  private final RoleDaoImpl roleDaoImpl;

  public RoleAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Role role,
      RoleDaoImpl roleDaoImpl) {
    this.role = role;
    this.roleDaoImpl = roleDaoImpl;
  }

  public String getName() {
    return role.getName();
  }

  public String getDescription() {
    return role.getDescription();
  }

  public void setDescription(String description) {
    role.setDescription(description);
    roleDaoImpl.updateRole(role);
  }

  public String getId() {
    return role.getId();
  }

  public void setName(String name) {
    role.setName(name);
    roleDaoImpl.updateRole(role);
  }

  public boolean isComposite() {
    return false;
  }

  public void addCompositeRole(RoleModel role) {

  }

  public void removeCompositeRole(RoleModel role) {

  }

  public Set<RoleModel> getComposites() {
    return null;
  }

  public boolean isClientRole() {
    return false;
  }

  public String getContainerId() {
    return null;
  }

  public RoleContainerModel getContainer() {
    return null;
  }

  public boolean hasRole(RoleModel role) {
    return false;
  }

  public void setSingleAttribute(String name, String value) {
    return;
  }

  public void setAttribute(String name, Collection<String> values) {
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
}
