package org.ironiq.postgres.daoImpl;

import org.ironiq.postgres.models.UserEntity;
import org.ironiq.postgres.daoInterfaces.CredentialDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.UUID;
import com.zaxxer.hikari.HikariDataSource;

public class CredentialDaoImpl implements CredentialDao {

  private HikariDataSource ds;

  public CredentialDaoImpl(HikariDataSource ds) {
    this.ds = ds;
  }

  public boolean validateCredentials(String username, String password) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement(
          "SELECT * FROM credential WHERE user_id=? AND value = crypt(?, password)");
      stmt.setString(1, username);
      stmt.setString(2, password);
      ResultSet rs = stmt.executeQuery();
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

  public boolean updateCredentials(String username, String password) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement(
          "UPDATE credential SET value=crypt(?, gen_salt('bf', 8)) WHERE user_id=?");
      stmt.setString(1, password);
      stmt.setString(2, username);
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

  public boolean isConfiguredFor(String username, String credentialType) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement("SELECT * FROM credential WHERE user_id=? AND type=?");
      stmt.setString(1, username);
      stmt.setString(2, credentialType);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        if (rs.getString("value") != null) {
          return true;
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
    return false;
  }

  public boolean disableCredentialType(String username, String credentialType) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement("UPDATE credential SET value=? WHERE user_id=? AND type=?");
      stmt.setString(1, null);
      stmt.setString(2, username);
      stmt.setString(3, credentialType);
      int res = stmt.executeUpdate();
      if (res == 1) {
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

}
