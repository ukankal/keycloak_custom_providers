package org.ironiq.postgres.daoInterfaces;

public interface CredentialDao {

  boolean validateCredentials(String username, String password);

  boolean updateCredentials(String username, String password);

  boolean isConfiguredFor(String username, String credentialType);

  boolean disableCredentialType(String username, String credentialType);

}
