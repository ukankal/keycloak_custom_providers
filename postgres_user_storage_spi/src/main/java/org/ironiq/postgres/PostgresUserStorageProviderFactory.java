/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates and other contributors as indicated by
 * the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ironiq.postgres;

import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.common.util.EnvUtil;
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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
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

    private static HikariDataSource ds;
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
            ds = createDataSourceFromConfig(configMap.getFirst(DB_URL),
                    configMap.getFirst(DATABASE_NAME), configMap.getFirst(USERNAME),
                    configMap.getFirst(PASSWORD));
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
            return new PostgresUserStorageProvider(session, model, ds);
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
        ds.close();
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

    private HikariDataSource createDataSourceFromConfig(String db_jdbcUrl, String database_name,
            String db_username, String db_password) throws Exception {
        // logger.info(
        // "createDataSourceFromConfig : db_jdbcUrl={0}, database_name={1},
        // db_username={2},db_password={3}",
        // db_jdbcUrl, database_name, db_username, db_password);
        logger.info("<<<<<<<<<<<< createDataSourceFromConfig" + db_jdbcUrl + database_name
                + db_username + db_password);
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
}
