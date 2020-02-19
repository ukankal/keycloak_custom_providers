package org.ironiq.postgres.adapter;

import org.ironiq.postgres.daoImpl.*;
import org.ironiq.postgres.models.*;
import org.ironiq.postgres.adapter.*;
import org.ironiq.postgres.daoImpl.*;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.ClientModel;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;
import java.util.logging.*;

public class GroupAdapter implements GroupModel {

  private final Group$ group;
  private final GroupDaoImpl groupDaoImpl;
  private final RoleDaoImpl roleDaoImpl;
  protected KeycloakSession session;
  protected ComponentModel model;
  protected RealmModel realm;

  private final Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

  public GroupAdapter(KeycloakSession session, RealmModel realm, ComponentModel model,
      Group$ group) {
    logger.info("groupId" + group.getId());
    this.group = group;
    this.session = session;
    this.realm = realm;
    this.model = model;
    this.groupDaoImpl = new GroupDaoImpl();
    this.roleDaoImpl = new RoleDaoImpl();
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
    return Collections.emptyList();
  }

  public Map<String, List<String>> getAttributes() {
    return Collections.emptyMap();
  }

  public GroupModel getParent() {
    logger.info("get Parent of :" + group.getName() + groupDaoImpl.getParent(group.getId()));
    Group$ parentGroup = groupDaoImpl.getParent(group.getId());
    if (parentGroup == null) {
      return null;
    } ;
    return new GroupAdapter(session, realm, model, groupDaoImpl.getParent(group.getId()));
  }

  public String getParentId() {
    logger.info("get ParentId of :" + group.getName() + groupDaoImpl.getParent(group.getId()));
    Group$ parentGroup = groupDaoImpl.getParent(group.getId());
    if (parentGroup == null) {
      return null;
    } ;
    return parentGroup.getId();
  }

  public Set<GroupModel> getSubGroups() {
    Set<GroupModel> subGroupModels = new HashSet<GroupModel>();
    Set<Group$> subGroups = groupDaoImpl.getSubGroups(group.getId());
    subGroups.forEach((group) -> {
      subGroupModels.add(new GroupAdapter(session, realm, model, group));
    });
    return subGroupModels;
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
    Set<RoleModel> realmRoles = new HashSet<RoleModel>();
    Set<Role> roles = roleDaoImpl.getRealmRoleMappingsForGroup(group.getId());
    if (roles != null) {
      roles.forEach((role) -> {
        realmRoles.add(new RoleAdapter(session, realm, model, role));
      });
    }
    return realmRoles;
  }

  public Set<RoleModel> getClientRoleMappings(ClientModel app) {
    Set<RoleModel> clientRoles = new HashSet<RoleModel>();
    Set<Role> roles = roleDaoImpl.getClientRoleMappingsForGroup(group.getId(), app.getClientId());
    if (roles != null) {
      roles.forEach((role) -> {
        clientRoles.add(new RoleAdapter(session, realm, model, role));
      });
    }
    return clientRoles;
  }


  // TODO:
  public boolean hasRole(RoleModel role) {
    return false;
  }

  public void grantRole(RoleModel role) {
    return;
  }

  public Set<RoleModel> getRoleMappings() {
    Set<RoleModel> allRoles = new HashSet<RoleModel>();
    Set<Role> roles = roleDaoImpl.getRoleMappingsForGroup(group.getId());
    if (roles != null) {
      roles.forEach((role) -> {
        allRoles.add(new RoleAdapter(session, realm, model, role));
      });
    }

    return allRoles;
  }

  public void deleteRoleMapping(RoleModel role) {
    return;
  }

}
