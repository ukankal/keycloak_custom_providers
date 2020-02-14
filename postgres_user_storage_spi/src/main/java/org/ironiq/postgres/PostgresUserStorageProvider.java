package org.ironiq.postgres;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.common.util.EnvUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.ClientModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.keycloak.storage.federated.UserFederatedStorageProvider;

import org.ironiq.postgres.daoImpl.*;
import org.ironiq.postgres.adapter.*;
import org.ironiq.postgres.models.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@JBossLog
public class PostgresUserStorageProvider
        implements UserStorageProvider, UserLookupProvider, CredentialInputValidator,
        CredentialInputUpdater, UserRegistrationProvider, UserQueryProvider {

    protected KeycloakSession session;
    protected Properties properties;
    protected ComponentModel model;

    // map of loaded users in this transaction
    protected Map<String, UserModel> loadedUsers = new HashMap<>();
    protected final Set<String> supportedCredentialTypes = new HashSet<>();
    private HikariDataSource ds;

    private UserDaoImpl userDaoImpl;
    private RoleDaoImpl roleDaoImpl;
    private GroupDaoImpl groupDaoImpl;
    private CredentialDaoImpl credentialDaoImpl;

    public PostgresUserStorageProvider(KeycloakSession session, ComponentModel model,
            String db_jdbcUrl, String database_name, String db_username, String db_password)
            throws Exception {
        this.session = session;
        this.model = model;
        supportedCredentialTypes.add(PasswordCredentialModel.TYPE);
        this.ds = createDataSourceFromConfig(db_jdbcUrl, database_name, db_username, db_password);
        this.userDaoImpl = new UserDaoImpl(this.ds);
        this.groupDaoImpl = new GroupDaoImpl(this.ds);
        this.roleDaoImpl = new RoleDaoImpl(this.ds);
        this.credentialDaoImpl = new CredentialDaoImpl(this.ds);
    }

    private HikariDataSource createDataSourceFromConfig(String db_jdbcUrl, String database_name,
            String db_username, String db_password) throws Exception {
        log.debugv(
                "createDataSourceFromConfig : db_jdbcUrl={0}, database_name={1}, db_username={2},db_password={3}",
                db_jdbcUrl, database_name, db_username, db_password);
        try {
            HikariConfig jdbcConfig = new HikariConfig();
            jdbcConfig.setPoolName("kctest");
            jdbcConfig.setJdbcUrl(db_jdbcUrl.concat(database_name));
            jdbcConfig.setUsername(db_username);
            jdbcConfig.setPassword(db_password);
            jdbcConfig.setLeakDetectionThreshold(500);
            jdbcConfig.setMaximumPoolSize(10);
            jdbcConfig.setMinimumIdle(10);
            jdbcConfig.setIdleTimeout(20000);
            return new HikariDataSource(jdbcConfig);
        } catch (Exception e) {
            throw e;
        }
    }



    // UserStorageProvider methods

    @Override
    public void preRemove(RealmModel realm) {
        log.debugv("pre-remove realm");
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        log.debugv("pre-remove group");
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        log.debugv("pre-remove role");
    }

    // UserLookupProvider methods

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.debugv("getUserByUsername: username: {0}", username);
        return new UserAdapter(session, realm, model, userDaoImpl.getUserByUsername(username),
                userDaoImpl, roleDaoImpl, groupDaoImpl);
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.debugv("getUserById: username: {0}", id);
        String externalId = StorageId.externalId(id);
        return new UserAdapter(session, realm, model, userDaoImpl.getUserById(externalId),
                userDaoImpl, roleDaoImpl, groupDaoImpl);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.debugv("getUserByEmail: username: {0}", email);
        return new UserAdapter(session, realm, model, userDaoImpl.getUserByEmail(email),
                userDaoImpl, roleDaoImpl, groupDaoImpl);
    }

    // UserQueryProvider methods

    @Override
    public int getUsersCount(RealmModel realm) {
        log.debug("getUsersCount");
        return userDaoImpl.getUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        log.debug("getUsers");
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.getAllUsers();
        users.forEach((user) -> {
            userModels.add(new UserAdapter(session, realm, model, user, userDaoImpl, roleDaoImpl,
                    groupDaoImpl));
        });
        return userModels;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        log.debugv("getUsers: firstResult {0}, maxResults {1}", firstResult, maxResults);
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.getAllUsers(firstResult, maxResults);
        users.forEach((user) -> {
            userModels.add(new UserAdapter(session, realm, model, user, userDaoImpl, roleDaoImpl,
                    groupDaoImpl));
        });
        return userModels;
    }

    // UserQueryProvider method implementations

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return searchForUser(search, realm, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult,
            int maxResults) {
        List<UserModel> users = new LinkedList<>();
        int i = 0;
        for (Object obj : properties.keySet()) {
            String username = (String) obj;
            if (!username.contains(search))
                continue;
            if (i++ < firstResult)
                continue;
            UserModel user = getUserByUsername(username, realm);
            users.add(user);
            if (users.size() >= maxResults)
                break;
        }
        return users;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return searchForUser(params, realm, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm,
            int firstResult, int maxResults) {
        // only support searching by username
        String usernameSearchString = params.get("username");
        if (usernameSearchString == null)
            return Collections.EMPTY_LIST;
        return searchForUser(usernameSearchString, realm, firstResult, maxResults);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult,
            int maxResults) {
        log.debugv("getGroupMembers: group {0}, firstResult {1}, maxResults {2}", group.getId(),
                firstResult, maxResults);
        // runtime automatically handles querying UserFederatedStorage
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users =
                groupDaoImpl.getGroupMembers(group.getId(), maxResults, firstResult);
        users.forEach((user) -> {
            userModels.add(new UserAdapter(session, realm, model, user, userDaoImpl, roleDaoImpl,
                    groupDaoImpl));
        });
        return userModels;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        // runtime automatically handles querying UserFederatedStorage
        log.debugv("getGroupMembers: group {0}", group.getId());
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = groupDaoImpl.getGroupMembers(group.getId());
        users.forEach((user) -> {
            userModels.add(new UserAdapter(session, realm, model, user, userDaoImpl, roleDaoImpl,
                    groupDaoImpl));
        });
        return userModels;
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue,
            RealmModel realm) {
        // runtime automatically handles querying UserFederatedStorage
        return Collections.EMPTY_LIST;
    }


    // UserRegistrationProvider method implementations

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        log.debugv("addUser: username {0}", username);
        UserEntity user = new UserEntity();
        user.setUsername(username);
        userDaoImpl.insertUser(user);
        return getUserByUsername(username, realm);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        log.debugv("removeUser: username {0}", user.getId());
        UserEntity userEntity = new UserEntity();
        String externalId = StorageId.externalId(user.getId());
        userEntity.setId(externalId);
        return userDaoImpl.deleteUser(userEntity);
    }



    // CredentialInputValidator methods

    public Set<String> getSupportedCredentialTypes() {
        log.debug("getSupportedCredentialTypes");
        return new HashSet<String>(this.supportedCredentialTypes);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.debugv("isConfiguredFor: userId={0}, credentialType={1}", user.getId(), credentialType);
        boolean isPasswordAuthDisabledForUser =
                credentialDaoImpl.isConfiguredFor(user.getUsername(), credentialType);
        return getSupportedCredentialTypes().contains(credentialType)
                && isPasswordAuthDisabledForUser;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.debugv("supportsCredentialType: credentialtype {0}", credentialType);
        return getSupportedCredentialTypes().contains(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        log.debugv("isValid: userId={0}, credentialType={1}", user.getId(), input.getType());
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
            return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        return credentialDaoImpl.validateCredentials(user.getUsername(),
                cred.getChallengeResponse());
    }

    // CredentialInputUpdater methods

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        log.debugv("updateCredential: userId={0}, credentialType={1}", user.getId(),
                input.getType());
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
            return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        return credentialDaoImpl.updateCredentials(user.getUsername(), cred.getChallengeResponse());
    }

    // TODO: handle exception
    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        log.debugv("disableCredentialType: userId={0}, credentialType={1}", user.getId(),
                credentialType);
        if (!supportsCredentialType(credentialType))
            return;
        if (credentialDaoImpl.disableCredentialType(user.getUsername(), credentialType)) {
            return;
        }
    }

    private static final Set<String> disableableTypes = new HashSet<>();

    static {
        disableableTypes.add(CredentialModel.PASSWORD);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        log.debug("getDisableableCredentialTypes");
        return disableableTypes;
    }

    @Override
    public void close() {
        log.debugv("closing");
    }

}


