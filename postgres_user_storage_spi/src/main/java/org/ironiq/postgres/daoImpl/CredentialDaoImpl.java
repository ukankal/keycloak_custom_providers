package org.ironiq.postgres.daoImpl;

import org.ironiq.postgres.PostgresUserStorageProviderFactory;
import org.ironiq.postgres.daoInterfaces.CredentialDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import com.zaxxer.hikari.HikariDataSource;

public class CredentialDaoImpl implements CredentialDao {

  private HikariDataSource ds;

  public CredentialDaoImpl(String realmName) {
    this.ds = PostgresUserStorageProviderFactory.getDataSource().get(realmName);
  }

  public boolean validateCredentials(String userId, String password) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn
          .prepareStatement("SELECT * FROM credential WHERE user_id=? AND value = crypt(?, value)");
      stmt.setObject(1, UUID.fromString(userId));
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

  public boolean updateCredentials(String userId, String password) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();

      stmt = conn.prepareStatement(
          "UPDATE credential SET value=crypt(?, gen_salt('bf', 8)) WHERE user_id=?");
      stmt.setString(1, password);
      stmt.setObject(2, UUID.fromString(userId));
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

  public boolean isConfiguredFor(String userId, String credentialType) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("SELECT * FROM credential WHERE user_id=? AND type=?");
      stmt.setObject(1, UUID.fromString(userId));
      stmt.setString(2, credentialType);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        if (rs.getBoolean("enabled") == true) {
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

  public boolean disableCredentialType(String userId, String credentialType) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = ds.getConnection();
      stmt = conn.prepareStatement("UPDATE credential SET enabled=? WHERE user_id=? AND type=?");
      stmt.setBoolean(1, false);
      stmt.setObject(2, UUID.fromString(userId));
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
