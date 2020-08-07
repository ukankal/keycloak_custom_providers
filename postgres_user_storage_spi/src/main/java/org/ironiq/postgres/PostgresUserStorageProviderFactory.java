package org.ironiq.postgres;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.keycloak.Config;
import org.keycloak.common.util.MultivaluedHashMap;

import java.util.List;
import java.util.HashMap;
import java.util.logging.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PostgresUserStorageProviderFactory
        implements UserStorageProviderFactory<PostgresUserStorageProvider> {

    public static final String PROVIDER_NAME = "IronIQ-postgres-db";
    // public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    // public static final String DATABASE_NAME = "keycloak_user_storage";
    // public static final String USERNAME = "postgres";
    // public static final String PASSWORD = "postgres";

    protected static final List<ProviderConfigProperty> configMetadata;
    private final Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

    // private static HikariDataSource ds;
    private static HashMap<String, HikariDataSource> realmDatasourceMap = new HashMap<String, HikariDataSource>();

    public static final String DB_URL = "db:url";
    public static final String DATABASE_NAME = "db:database";
    public static final String USERNAME = "db:username";
    public static final String PASSWORD = "db:password";

    static {
        configMetadata = ProviderConfigurationBuilder.create()
                // Connection Name
                .property().name(DB_URL).type(ProviderConfigProperty.STRING_TYPE)
                .label("JDBC Connection URL").defaultValue("")
                .helpText("DB_URL ex: jdbc:postgresql://localhost:5432/ ").add()

                // Connection Database
                .property().name(DATABASE_NAME).type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Name").add()

                // DB Username
                .property().name(USERNAME).type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Username").add()

                // DB Password
                .property().name(PASSWORD).type(ProviderConfigProperty.PASSWORD)
                .label("Database Password").add()

                .build();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
        logger.info("<<<<<<<<<<<<<<< onCreate of factory" + DB_URL + DATABASE_NAME + USERNAME
                + PASSWORD);
        MultivaluedHashMap<String, String> configMap = model.getConfig();
        try {
            HikariDataSource ds = createDataSourceFromConfig(configMap.getFirst(DB_URL),
                    configMap.getFirst(DATABASE_NAME), configMap.getFirst(USERNAME),
                    configMap.getFirst(PASSWORD));
            realmDatasourceMap.put(realm.getName(),ds);
        } catch (Exception e) {
            throw e;
        } finally {
            return;
        }
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm,
            ComponentModel config) throws ComponentValidationException {
        MultivaluedHashMap<String, String> configMap = config.getConfig();
        if (StringUtils.isBlank(configMap.getFirst(DB_URL))) {
            throw new ComponentValidationException("Jdbc Url is empty.");
        }
        if (StringUtils.isBlank(configMap.getFirst(DATABASE_NAME))) {
            throw new ComponentValidationException("Database name empty.");
        }
        if (StringUtils.isBlank(configMap.getFirst(USERNAME))) {
            throw new ComponentValidationException("Database username empty.");
        }
        if (StringUtils.isBlank(configMap.getFirst(PASSWORD))) {
            throw new ComponentValidationException("Database password empty.");
        }
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

    @Override
    public PostgresUserStorageProvider create(KeycloakSession session, ComponentModel model) {

        MultivaluedHashMap<String, String> config = model.getConfig();
        logger.info("create Instance: {0}, {1}, {2},{3}" + config.getFirst(DB_URL)
                + config.getFirst(DATABASE_NAME) + config.getFirst(USERNAME)
                + config.getFirst(PASSWORD));
        try {
            return new PostgresUserStorageProvider(session, model);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            return null;
        }
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        logger.info("<<<<<<<<<<<<<<<<<< In postinit");
    }

    @Override
    public void close() {
        logger.info("<<<<<<<<<<<<<<<<<< Closing factory");
        for (HashMap.Entry<String, HikariDataSource> entry : realmDatasourceMap.entrySet()) {
            HikariDataSource value = entry.getValue();
            value.close();
        }
    }

    @Override
    public void init(Config.Scope config) {
        logger.info("<<<<<<<<<<<<<<<<<< Init factory");
    }

    @Override
    public PostgresUserStorageProvider create(KeycloakSession session) {
        logger.info("<<<<<<<<<<<<<<<<<< create provider instance");
        return null;
    }

    @Override
    public String getHelpText() {
        logger.info("<<<<<<<<<<<<<<<<<< Help text");
        return null;
    }

    public static HashMap<String, HikariDataSource> getDataSource() {
        return realmDatasourceMap;
    }

    private HikariDataSource createDataSourceFromConfig(String db_jdbcUrl, String database_name,
            String db_username, String db_password) throws Exception {
        logger.info("<<<<<<<<<<<< createDataSourceFromConfig" + db_jdbcUrl + database_name
                + db_username + db_password);
        try {
            HikariConfig jdbcConfig = new HikariConfig();
            jdbcConfig.setPoolName("keycloak_"+database_name+"_user_storage_spi");
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
}
