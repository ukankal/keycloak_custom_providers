package org.ironiq.postgres;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import org.ironiq.postgres.daoImpl.*;
import org.ironiq.postgres.adapter.*;
import org.ironiq.postgres.models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.*;

public class PostgresUserStorageProvider
        implements UserStorageProvider, UserLookupProvider, CredentialInputValidator,
        CredentialInputUpdater, UserRegistrationProvider, UserQueryProvider {

    protected KeycloakSession session;
    protected Properties properties;
    protected ComponentModel model;

    // map of loaded users in this transaction
    protected Map<String, UserModel> loadedUsers = new HashMap<>();
    protected final Set<String> supportedCredentialTypes = new HashSet<>();

    private UserDaoImpl userDaoImpl;
    private GroupDaoImpl groupDaoImpl;
    private CredentialDaoImpl credentialDaoImpl;

    private final Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

    public PostgresUserStorageProvider(KeycloakSession session, ComponentModel model)
            throws Exception {
        logger.info("PostgresUserStorageProvider constructor");
        this.session = session;
        this.model = model;
        supportedCredentialTypes.add(PasswordCredentialModel.TYPE);
        // supportedCredentialTypes.add(OTPCredentialModel.TYPE);
        String realmName = session.getContext().getRealm().getName();
        this.userDaoImpl = new UserDaoImpl(realmName);
        this.groupDaoImpl = new GroupDaoImpl(realmName);
        this.credentialDaoImpl = new CredentialDaoImpl(realmName);
    }

    // UserStorageProvider methods

    @Override
    public void preRemove(RealmModel realm) {
        logger.info("pre-remove realm");
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        logger.info("pre-remove group");
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        logger.info("pre-remove role");
    }

    // UserLookupProvider methods

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        logger.info("getUserByUsername: username: {0}" + username);
        UserEntity user = userDaoImpl.getUserByUsername(username);
        if (user != null) {
            return new UserAdapter(session, realm, model, user);
        }
        return null;
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        logger.info("getUserById: username: {0}" + id);
        String externalId = StorageId.externalId(id);
        UserEntity user = userDaoImpl.getUserById(externalId);
        if (user != null) {
            return new UserAdapter(session, realm, model, user);
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        logger.info("getUserByEmail: username: {0}" + email);
        UserEntity user = userDaoImpl.getUserByEmail(email);
        if (user != null) {
            return new UserAdapter(session, realm, model, user);
        }
        return null;
    }

    // UserQueryProvider methods

    @Override
    public int getUsersCount(RealmModel realm) {
        logger.info("getUsersCount");
        return userDaoImpl.getUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        logger.info("getUsers");
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.getAllUsers();
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        logger.info("getUsers: firstResult {0}, maxResults {1}" + firstResult + maxResults);
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.getAllUsers(firstResult, maxResults);
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    // UserQueryProvider method implementations

    @Override
    public List<UserModel> searchForUser(String searchTerm, RealmModel realm) {
        logger.info("searchForUser : " + searchTerm);
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.searchForUser(searchTerm);
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    @Override
    public List<UserModel> searchForUser(String searchTerm, RealmModel realm, int firstResult,
            int maxResults) {
        logger.info("searchForUser Term: " + searchTerm + "offset :" + firstResult + "limit : "
                + maxResults);
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.searchForUser(searchTerm, firstResult, maxResults);
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        params.forEach((key, value) -> logger.info("key :" + key + " " + "value : " + value));
        logger.info("searchForUser: without pagination");
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.searchForUser(params);
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm,
            int firstResult, int maxResults) {
        params.forEach((key, value) -> logger.info("key :" + key + " " + "value : " + value));
        logger.info("searchForUser: with pagination");

        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = userDaoImpl.searchForUser(params, firstResult, maxResults);
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult,
            int maxResults) {
        logger.info("getGroupMembers: group {0}, firstResult {1}, maxResults {2}" + group.getId()
                + firstResult + maxResults);

        // runtime automatically handles querying UserFederatedStorage
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users =
                groupDaoImpl.getGroupMembers(group.getId(), maxResults, firstResult);
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        // runtime automatically handles querying UserFederatedStorage
        logger.info("getGroupMembers: group {0}" + group.getId());
        List<UserModel> userModels = new ArrayList<UserModel>();
        List<UserEntity> users = groupDaoImpl.getGroupMembers(group.getId());
        if (users != null) {
            users.forEach((user) -> {
                userModels.add(new UserAdapter(session, realm, model, user));
            });
        }
        return userModels;
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue,
            RealmModel realm) {
        // runtime automatically handles querying UserFederatedStorage
        return Collections.emptyList();
    }


    // UserRegistrationProvider method implementations

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        logger.info("addUser: username {0}" + username);
        UserEntity user = new UserEntity();
        user.setUsername(username);
        userDaoImpl.insertUser(user);
        return getUserByUsername(username, realm);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        logger.info("removeUser: username {0}" + user.getId());
        UserEntity userEntity = new UserEntity();
        String externalId = StorageId.externalId(user.getId());
        userEntity.setId(externalId);
        return userDaoImpl.deleteUser(userEntity);
    }

    // CredentialInputValidator methods

    public Set<String> getSupportedCredentialTypes() {
        logger.info("getSupportedCredentialTypes");
        return new HashSet<String>(this.supportedCredentialTypes);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        logger.info(
                "isConfiguredFor: userId={0}, credentialType={1}" + user.getId() + credentialType);
        String externalId = StorageId.externalId(user.getId());
        boolean isPasswordAuthDisabledForUser =
                credentialDaoImpl.isConfiguredFor(externalId, credentialType);
        return getSupportedCredentialTypes().contains(credentialType)
                && isPasswordAuthDisabledForUser;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        logger.info("supportsCredentialType: credentialtype {0}" + credentialType);
        return getSupportedCredentialTypes().contains(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        logger.info("isValid: userId={0}, credentialType={1}" + user.getId() + input.getType());
        String externalId = StorageId.externalId(user.getId());
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
            return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        return credentialDaoImpl.validateCredentials(externalId, cred.getChallengeResponse());
    }

    // CredentialInputUpdater methods

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        logger.info("updateCredential: userId={0}, credentialType={1}" + user.getId()
                + input.getType());
        String externalId = StorageId.externalId(user.getId());
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
            return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        return credentialDaoImpl.updateCredentials(externalId, cred.getChallengeResponse());
    }

    // TODO: handle exception
    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        logger.info("disableCredentialType: userId={0}, credentialType={1}" + user.getId()
                + credentialType);
        String externalId = StorageId.externalId(user.getId());
        if (!supportsCredentialType(credentialType))
            return;
        if (credentialDaoImpl.disableCredentialType(externalId, credentialType)) {
            return;
        }
    }

    private static final Set<String> disableableTypes = new HashSet<>();

    static {
        disableableTypes.add(CredentialModel.PASSWORD);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        logger.info("getDisableableCredentialTypes");
        return disableableTypes;
    }

    @Override
    public void close() {
        logger.info("closing");
    }

}


