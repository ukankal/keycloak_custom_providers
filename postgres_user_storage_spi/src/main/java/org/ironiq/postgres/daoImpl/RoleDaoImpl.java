package org.ironiq.postgres.daoImpl;

import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.models.Role;
import org.ironiq.postgres.daoInterfaces.RoleDao;

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
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.zaxxer.hikari.HikariDataSource;

public class RoleDaoImpl implements RoleDao {

  private final HikariDataSource ds;

  public RoleDaoImpl(HikariDataSource ds) {
    this.ds = ds;
  }

  public boolean updateRole(Role role) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement(
          "UPDATE roles SET name=?,description=?, client_role=?, default_role=?, client_id=? WHERE id=?");
      stmt.setString(1, role.getName());
      stmt.setString(2, role.getDescription());
      stmt.setBoolean(3, role.getClientRole());
      stmt.setBoolean(4, role.getDefaultRole());
      stmt.setString(5, role.getClientId());
      stmt.setString(7, role.getId());

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

  public List<UserEntity> getRoleMembers(String roleId, int offset, int limit) {
    Connection conn = null;
    PreparedStatement stmt = null;
    List<UserEntity> users = new ArrayList<UserEntity>();
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "select * from user_entity where id in (select user_id from user_role_mapping where role_id = ? UNION select user_id from user_group_membership where group_id in (select group_id from group_role_mapping where role_id = ?)) limit ? offset ?");
      stmt.setString(1, roleId);
      stmt.setInt(1, limit);
      stmt.setInt(1, offset);
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

  public Set<Role> getClientRoleMappings(String clientId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    Set<Role> roles = new HashSet<Role>();
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("select * from roles where client_id=?");
      stmt.setString(1, clientId);
      rs = stmt.executeQuery();
      while (rs.next()) {
        roles.add(extractRoleFromResultSet(rs));
      }
      return roles;
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

  public Set<Role> getRoleMappings(String userId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    Set<Role> roles = new HashSet<Role>();
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "select * from roles where id in (select role_id from user_role_mapping where user_id = ? UNION select role_id from group_role_mapping where group_id in (select group_id from user_group_membership where user_id = ?))");
      stmt.setString(1, userId);
      rs = stmt.executeQuery();
      while (rs.next()) {
        roles.add(extractRoleFromResultSet(rs));
      }
      return roles;
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

  public boolean grantRoleToUser(String userId, String roleId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("INSERT INTO user_role_mapping (role_id,user_id) VALUES (?, ?)");
      stmt.setString(1, roleId);
      stmt.setString(2, userId);

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

  public boolean revokeRoleToUser(String userId, String roleId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt =
          conn.prepareStatement("DELETE FROM user_role_mapping WHERE role_id = ? AND user_id = ?");
      stmt.setString(1, roleId);
      stmt.setString(2, userId);

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

  public boolean hasRole(String userId, String roleId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn
          .prepareStatement("select * from user_role_mapping WHERE role_id = ? AND user_id = ?");
      stmt.setString(1, roleId);
      stmt.setString(2, userId);
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

  private UserEntity extractUserFromResultSet(ResultSet rs) throws SQLException {
    UserEntity user = new UserEntity();
    user.setId(rs.getString("id"));
    user.setEmail(rs.getString("email"));
    user.setEmailVerified(rs.getBoolean("email_verified"));
    user.setEnabled(rs.getBoolean("enabled"));
    user.setFirstName(rs.getString("first_name"));
    user.setLastName(rs.getString("last_name"));
    user.setUsername(rs.getString("username"));
    user.setCreatedTimestamp(rs.getString("created_timestamp"));
    return user;
  }

  private Role extractRoleFromResultSet(ResultSet rs) throws SQLException {
    Role role = new Role();
    role.setId(rs.getString("id"));
    role.setClientRole(rs.getBoolean("client_role"));
    role.setName(rs.getString("name"));
    role.setDescription(rs.getString("description"));
    role.setClientId(rs.getString("client_id"));
    role.setDefaultRole(rs.getBoolean("default_role"));
    return role;
  }

}
