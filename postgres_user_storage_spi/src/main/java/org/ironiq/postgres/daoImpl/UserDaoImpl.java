package org.ironiq.postgres.daoImpl;

import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.daoInterfaces.UserDao;

import org.keycloak.models.UserModel;

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

public class UserDaoImpl implements UserDao {

  private HikariDataSource ds;

  public UserDaoImpl(HikariDataSource ds) {
    this.ds = ds;
  }

  public UserEntity getUserById(String id) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement("select * from user_entity where id = ? limit 1");
      stmt.setString(1, id);
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
      conn.setAutoCommit(false);
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
      conn.setAutoCommit(false);
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
      conn.setAutoCommit(false);
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



  private List<UserEntity> getUsers(Integer start, Integer end) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
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

  public List<UserEntity> getAllUsers() {
    return getUsers(null, null);
  }

  public List<UserEntity> getAllUsers(int start, int max) {
    return getUsers((Integer) start, (Integer) max);
  }

  public boolean insertUser(UserEntity user) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement("INSERT INTO user_entity (username) values(?)");
      stmt.setString(1, user.getUsername());
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

  public boolean updateUser(UserEntity user) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement(
          "UPDATE user_entity SET email=?,email_verified=?, enabled=?, first_name=?, last_name=?, username=? WHERE id=?");
      stmt.setString(1, user.getEmail());
      stmt.setBoolean(2, user.getEmailVerified());
      stmt.setBoolean(3, user.getEnabled());
      stmt.setString(4, user.getFirstName());
      stmt.setString(5, user.getLastName());
      stmt.setString(6, user.getUsername());
      stmt.setString(7, user.getId());

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
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement("DELETE FROM user_entity where id=?");
      stmt.setString(1, user.getId());
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

  public void addRequiredAction(String userId, String action) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement("SELECT * FROM user_entity where id=?");
      stmt.setString(1, userId);
      rs = stmt.executeQuery();
      if (rs.next()) {
        stmt = conn.prepareStatement(
            "INSERT INTO user_required_action (user_id,required_action) VALUES (?, ?)");
        stmt.setString(1, userId);
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
      conn.setAutoCommit(false);
      stmt1 = conn.prepareStatement("SELECT * FROM user_entity where id=?");
      stmt1.setString(1, userId);
      rs = stmt1.executeQuery();
      if (rs.next()) {
        stmt2 = conn.prepareStatement(
            "INSERT INTO user_required_action (user_id,required_action) VALUES (?, ?)");
        stmt2.setString(1, userId);
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
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement("SELECT required_action FROM user_required_action where id=?");
      stmt.setString(1, userId);
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
      conn.setAutoCommit(false);

      stmt = conn.prepareStatement(
          "DELETE FROM user_required_action WHERE user_id=? AND required_action=?");
      stmt.setString(1, userId);
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
      conn.setAutoCommit(false);

      stmt = conn.prepareStatement(
          "DELETE FROM user_required_action WHERE user_id=? AND required_action=?");
      stmt.setString(1, userId);
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

}