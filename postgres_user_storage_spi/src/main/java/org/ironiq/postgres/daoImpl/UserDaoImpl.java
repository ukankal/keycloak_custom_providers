package org.ironiq.postgres.daoImpl;

import org.ironiq.postgres.PostgresUserStorageProviderFactory;
import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.daoInterfaces.UserDao;

import org.keycloak.models.UserModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import com.zaxxer.hikari.HikariDataSource;

public class UserDaoImpl implements UserDao {

  private HikariDataSource ds;

  public UserDaoImpl(String realmName) {
    this.ds = PostgresUserStorageProviderFactory.getDataSource().get(realmName);
  }

  public UserEntity getUserById(String id) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("select * from user_entity where id = ? limit 1");
      stmt.setObject(1, UUID.fromString(id));
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return extractUserFromResultSet(rs);
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
    return null;
  }

  public UserEntity getUserByUsername(String username) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("select * from user_entity where username = ? limit 1");
      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return extractUserFromResultSet(rs);
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
    return null;
  }

  public UserEntity getUserByEmail(String email) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("select * from user_entity where email = ? limit 1");
      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return extractUserFromResultSet(rs);
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
    return null;
  }

  public int getUsersCount() {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("select count(*) from user_entity");
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        {
          return rs.getInt("count");
        }
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
    return 0;
  }

  public List<UserEntity> getAllUsers() {
    return getUsers(null, null);
  }

  public List<UserEntity> getAllUsers(int start, int max) {
    return getUsers((Integer) start, (Integer) max);
  }

  public boolean insertUser(UserEntity user) {
    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement stmt1 = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("INSERT INTO user_entity (username) values(?)");
      stmt.setString(1, user.getUsername());
      int rs = stmt.executeUpdate();
      if (rs == 1) {
        UserEntity user$ = getUserByUsername(user.getUsername());
        stmt1 = conn.prepareStatement("INSERT INTO credential (type, user_id) values(?, ?)");
        stmt1.setString(1, "password");
        stmt1.setObject(2, UUID.fromString(user$.getId()));
        int res = stmt1.executeUpdate();
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

  public boolean updateUser(UserEntity user) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "UPDATE user_entity SET email=?,email_verified=?, enabled=?, first_name=?, last_name=?, username=? WHERE id=?");
      stmt.setString(1, user.getEmail());
      stmt.setBoolean(2, user.getEmailVerified());
      stmt.setBoolean(3, user.getEnabled());
      stmt.setString(4, user.getFirstName());
      stmt.setString(5, user.getLastName());
      stmt.setString(6, user.getUsername());
      stmt.setObject(7, UUID.fromString(user.getId()));

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

  public boolean deleteUser(UserEntity user) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("DELETE FROM user_entity where id=?");
      stmt.setObject(1, UUID.fromString(user.getId()));
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

  public void addRequiredAction(String userId, String action) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("SELECT * FROM user_entity where id=?");
      stmt.setObject(1, UUID.fromString(userId));
      rs = stmt.executeQuery();
      if (rs.next()) {
        stmt = conn.prepareStatement(
            "INSERT INTO user_required_action (user_id,required_action) VALUES (?, ?)");
        stmt.setObject(1, UUID.fromString(userId));
        stmt.setString(2, action);
        int res = stmt.executeUpdate();
        return;
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
    return;
  }

  public void addRequiredAction(String userId, UserModel.RequiredAction action) {
    Connection conn = null;
    PreparedStatement stmt1 = null;
    PreparedStatement stmt2 = null;
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt1 = conn.prepareStatement("SELECT * FROM user_entity where id=?");
      stmt1.setObject(1, UUID.fromString(userId));
      rs = stmt1.executeQuery();
      if (rs.next()) {
        stmt2 = conn.prepareStatement(
            "INSERT INTO user_required_action (user_id,required_action) VALUES (?, ?)");
        stmt2.setObject(1, UUID.fromString(userId));
        stmt2.setString(2, action.toString());
        int res = stmt2.executeUpdate();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stmt1 != null && stmt2 != null) {
        try {
          stmt1.close();
          stmt2.close();
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
    return;
  }

  public Set<String> getRequiredActions(String userId) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Set<String> required_actions = new HashSet<String>();
    try {
      conn = ds.getConnection();
      stmt =
          conn.prepareStatement("SELECT required_action FROM user_required_action where user_id=?");
      stmt.setObject(1, UUID.fromString(userId));
      rs = stmt.executeQuery();
      while (rs.next()) {
        required_actions.add(rs.getString("required_action"));
      }
      return required_actions;
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

  public void removeRequiredAction(String userId, String action) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "DELETE FROM user_required_action WHERE user_id=? AND required_action=?");
      stmt.setObject(1, UUID.fromString(userId));
      stmt.setString(2, action);
      int rs = stmt.executeUpdate();
      if (rs == 1) {
        return;
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
    return;
  }

  public void removeRequiredAction(String userId, UserModel.RequiredAction action) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement(
          "DELETE FROM user_required_action WHERE user_id=? AND required_action=?");
      stmt.setObject(1, UUID.fromString(userId));
      stmt.setString(2, action.toString());
      int rs = stmt.executeUpdate();
      if (rs == 1) {
        return;
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
    return;
  }

  public List<UserEntity> searchForUser(String searchTerm) {
    return searchForUser(searchTerm, null, null);
  }

  public List<UserEntity> searchForUser(String searchTerm, int start, int max) {
    return searchForUser(searchTerm, (Integer) start, (Integer) max);
  }

  public List<UserEntity> searchForUser(Map<String, String> params) {
    return searchForUser(params, null, null);
  }

  public List<UserEntity> searchForUser(Map<String, String> params, int start, int max) {
    return searchForUser(params, (Integer) start, (Integer) max);
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

  private List<UserEntity> getUsers(Integer start, Integer end) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      if (start != null && end != null) {
        stmt = conn.prepareStatement("select * from user_entity limit ? offset ?");
        stmt.setInt(1, end - start);
        stmt.setInt(2, start);
      } else {
        stmt = conn.prepareStatement("select * from user_entity");
      }
      ResultSet rs = stmt.executeQuery();
      List<UserEntity> users = new ArrayList<UserEntity>();
      while (rs.next()) {
        {
          UserEntity user = extractUserFromResultSet(rs);
          users.add(user);
        }
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

  private List<UserEntity> searchForUser(String searchTerm, Integer firstResult,
      Integer maxResults) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      if (firstResult != null && maxResults != null) {
        stmt = conn.prepareStatement(
            "select * from user_entity where (username LIKE ? OR  first_name LIKE ? OR  last_name like ? OR  email like ?) limit ? offset ?");
        stmt.setString(1, "%" + searchTerm + "%");
        stmt.setString(2, "%" + searchTerm + "%");
        stmt.setString(3, "%" + searchTerm + "%");
        stmt.setString(4, "%" + searchTerm + "%");
        stmt.setInt(5, maxResults);
        stmt.setInt(6, firstResult);
      } else {
        stmt = conn.prepareStatement("select * from user_entity");
      }
      ResultSet rs = stmt.executeQuery();
      List<UserEntity> users = new ArrayList<UserEntity>();
      while (rs.next()) {
        {
          UserEntity user = extractUserFromResultSet(rs);
          users.add(user);
        }
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

  private List<UserEntity> searchForUser(Map<String, String> params, Integer firstResult,
      Integer maxResults) {
    Connection conn = null;
    PreparedStatement stmt = null;
    String query = null;
    try {
      conn = ds.getConnection();
      if (firstResult != null && maxResults != null) {
        int len = params.size();
        query = "select * from user_entity";
        if (len > 0) {
          for (int i = 0; i < len; i++) {
            if (i == 0) {
              query = query + "where ( ? LIKE ?";
            } else if (i == len - 1) {
              query = query + "OR" + "? LIKE ?)" + "limit ? offset ?";
            }
            query = query + "OR" + "? LIKE ?";
          }
          stmt = conn.prepareStatement(query);

          String[] keys = params.keySet().toArray(new String[0]);
          String[] values = params.values().toArray(new String[0]);

          for (int i = 0; i < keys.length; i++) {
            stmt.setString(i + 1, keys[i]);
          }

          for (int i = 0; i < values.length; i++) {
            stmt.setString(i + 2, "%" + values[i] + "%");
          }

          stmt.setInt(len + 1, maxResults);
          stmt.setInt(len + 2, firstResult);
        } else {
          query = "select * from user_entity limit ? offset ?";
          stmt = conn.prepareStatement(query);
          stmt.setInt(1, maxResults);
          stmt.setInt(2, firstResult);
        }
      } else {
        int len = params.size();
        if (len > 0) {
          query = "select * from user_entity where (";
          for (int i = 0; i < len; i++) {
            if (i == 0) {
              query = query + "? LIKE ?";
            } else if (i == len - 1) {
              query = query + "OR" + "? LIKE ?)";
            }
            query = query + "OR" + "? LIKE ?";
          }
          stmt = conn.prepareStatement(query);

          String[] keys = params.keySet().toArray(new String[0]);
          String[] values = params.values().toArray(new String[0]);

          for (int i = 0; i < keys.length; i++) {
            stmt.setString(i + 1, keys[i]);
          }

          for (int i = 0; i < values.length; i++) {
            stmt.setString(i + 2, "%" + values[i] + "%");
          }
        } else {
          query = "select * from user_entity";
          stmt = conn.prepareStatement(query);
        }
      }
      ResultSet rs = stmt.executeQuery();
      List<UserEntity> users = new ArrayList<UserEntity>();
      while (rs.next()) {
        {
          UserEntity user = extractUserFromResultSet(rs);
          users.add(user);
        }
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


}
