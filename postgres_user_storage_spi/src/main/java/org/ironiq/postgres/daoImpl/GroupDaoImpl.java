package org.ironiq.postgres.daoImpl;

import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.models.Group;
import org.ironiq.postgres.daoInterfaces.GroupDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.zaxxer.hikari.HikariDataSource;

public class GroupDaoImpl implements GroupDao {

  private final HikariDataSource ds;

  public GroupDaoImpl(HikariDataSource ds) {
    this.ds = ds;
  }

  public boolean updateGroup(Group group) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();

      stmt = conn
          .prepareStatement("UPDATE groups SET name=?,parent_group=?, default_group=? WHERE id=?");
      stmt.setString(1, group.getName());
      stmt.setString(2, group.getParentGroup());
      stmt.setBoolean(3, group.getDefaultGroup());
      stmt.setObject(4, UUID.fromString(group.getId()));

      int rs = stmt.executeUpdate();
      if (rs == 1) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  public List<UserEntity> getGroupMembers(String groupId, int limit, int offset) {
    return getGroupMembers(groupId, (Integer) limit, (Integer) offset);
  }

  public List<UserEntity> getGroupMembers(String groupId) {
    return getGroupMembers(groupId, null, null);
  }

  public Set<Group> getGroups(String userId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    Set<Group> groups = new HashSet<Group>();
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "select * from groups where id in (select group_id from user_group_membership where user_id = ?)");
      stmt.setObject(1, UUID.fromString(userId));
      rs = stmt.executeQuery();
      while (rs.next()) {
        groups.add(extractGroupFromResultSet(rs));
      }
      return groups;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public boolean isUserMemberOf(String userId, String groupId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "select * from user_group_membership where user_id = ? AND group_id = ?");
      stmt.setObject(1, UUID.fromString(userId));
      stmt.setObject(2, UUID.fromString(groupId));
      rs = stmt.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  public boolean addUserToGroup(String userId, String groupId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn
          .prepareStatement("INSERT INTO user_group_membership (group_id,user_id) VALUES (?, ?)");
      stmt.setObject(2, UUID.fromString(userId));
      stmt.setObject(1, UUID.fromString(groupId));
      int rs = stmt.executeUpdate();
      if (rs == 1) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  public boolean removeUserFromGroup(String userId, String groupId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn
          .prepareStatement("DELETE FROM user_group_membership WHERE group_id = ? AND user_id = ?");
      stmt.setObject(2, UUID.fromString(userId));
      stmt.setObject(1, UUID.fromString(groupId));

      int rs = stmt.executeUpdate();
      if (rs == 1) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  private List<UserEntity> getGroupMembers(String groupId, Integer limit, Integer offset) {
    Connection conn = null;
    PreparedStatement stmt = null;
    List<UserEntity> users = new ArrayList<UserEntity>();
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "select * from user_entity where id IN (select user_id from user_group_membership where group_id = ?) limit ? offset ?");
      stmt.setObject(1, UUID.fromString(groupId));
      stmt.setInt(2, limit);
      stmt.setInt(3, offset);
      rs = stmt.executeQuery();
      while (rs.next()) {
        users.add(extractUserFromResultSet(rs));
      }
      return users;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  private Group extractGroupFromResultSet(ResultSet rs) throws SQLException {
    Group group = new Group();
    group.setId(rs.getObject("id", java.util.UUID.class).toString());
    group.setName(rs.getString("name"));
    group.setParentGroup(rs.getString("parent_group"));
    group.setDefaultGroup(rs.getBoolean("default_group"));
    return group;
  }

  private UserEntity extractUserFromResultSet(ResultSet rs) throws SQLException {
    UserEntity user = new UserEntity();
    user.setId(rs.getObject("id", java.util.UUID.class).toString());
    user.setEmail(rs.getString("email"));
    user.setEmailVerified(rs.getBoolean("email_verified"));
    user.setEnabled(rs.getBoolean("enabled"));
    user.setFirstName(rs.getString("first_name"));
    user.setLastName(rs.getString("last_name"));
    user.setUsername(rs.getString("username"));
    user.setCreatedTimestamp(rs.getString("created_timestamp"));
    return user;
  }

}
