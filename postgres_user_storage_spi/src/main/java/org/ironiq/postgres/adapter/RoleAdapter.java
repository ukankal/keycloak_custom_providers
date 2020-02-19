package org.ironiq.postgres.adapter;

import org.ironiq.postgres.daoImpl.*;
import org.ironiq.postgres.models.*;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.RoleContainerModel;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;

public class RoleAdapter implements RoleModel {

  private final Role role;
  private final RoleDaoImpl roleDaoImpl;
  protected KeycloakSession session;
  protected ComponentModel model;
  protected RealmModel realm;

  public RoleAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Role role) {
    this.role = role;
    this.roleDaoImpl = new RoleDaoImpl();
    this.session = session;
    this.realm = realm;
    this.model = model;
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
    return Collections.emptySet();
  }

  public boolean isClientRole() {
    return roleDaoImpl.isClientRole(role.getId());
  }

  public String getContainerId() {
    return getContainer().getId();
  }

  public RoleContainerModel getContainer() {
    if (isClientRole()) {
      String client_name = roleDaoImpl.getClientName(role.getId());
      if (client_name != null) {
        return realm.getClientByClientId(client_name);
      }
    }
    return realm;
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
    return Collections.emptyList();
  }

  public Map<String, List<String>> getAttributes() {
    return Collections.emptyMap();
  }
}
