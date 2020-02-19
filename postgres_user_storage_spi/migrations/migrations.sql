CREATE TABLE roles (id UUID DEFAULT gen_random_uuid(), name CHARACTER VARYING(255), description CHARACTER VARYING(255), default_role BOOLEAN DEFAULT false NOT NULL, client_role BOOLEAN DEFAULT false NOT NULL,  client_id uuid NOT NULL, CONSTRAINT pk_roles PRIMARY KEY (id), CONSTRAINT fk_roles_clients FOREIGN KEY (client_id) REFERENCES "clients" ("id") ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT UK_roles_name_client_id UNIQUE (name, client_id));

CREATE TABLE clients (id UUID DEFAULT gen_random_uuid(), client_name CHARACTER VARYING(255), enabled BOOLEAN DEFAULT false NOT NULL, CONSTRAINT pk_clients PRIMARY KEY (id));

CREATE TABLE groups (id UUID DEFAULT gen_random_uuid(), name CHARACTER VARYING(255), parent_group UUID, default_group BOOLEAN DEFAULT false NOT NULL, CONSTRAINT pk_groups PRIMARY KEY (id), CONSTRAINT sibling_names UNIQUE ( parent_group, name));

CREATE TABLE user_role_mapping (role_id UUID NOT NULL, user_id UUID NOT NULL, CONSTRAINT pk_user_role_mapping PRIMARY KEY (role_id, user_id),CONSTRAINT fk_user_role_mapping_roles FOREIGN KEY (role_id) REFERENCES "roles" ("id") ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT fk_user_role_mapping_user_entity FOREIGN KEY (user_id) REFERENCES "user_entity" ("id")ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE user_group_membership (group_id UUID NOT NULL, user_id UUID NOT NULL, CONSTRAINT pk_user_group_membership PRIMARY KEY (group_id, user_id), CONSTRAINT fk_user_group_membership_groups FOREIGN KEY (group_id) REFERENCES "groups" ("id")ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT fk_user_group_membership_user_entity FOREIGN KEY (user_id) REFERENCES "user_entity" ("id") ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE group_role_mapping (role_id UUID NOT NULL, group_id UUID NOT NULL, CONSTRAINT pk_group_role_mapping PRIMARY KEY (role_id, group_id), CONSTRAINT fk_group_role_mappping_groups FOREIGN KEY (group_id) REFERENCES "groups" ("id") ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT fk_group_role_mapping_roles FOREIGN KEY (role_id) REFERENCES "roles" ("id") ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE user_entity (id UUID DEFAULT gen_random_uuid() NOT NULL, first_name CHARACTER VARYING(255), last_name CHARACTER VARYING(255),  username CHARACTER VARYING(255) NOT NULL, email CHARACTER VARYING(255), email_verified BOOLEAN DEFAULT false NOT NULL, enabled BOOLEAN DEFAULT false NOT NULL, created_timestamp TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT now() NOT NULL, CONSTRAINT pk_user_entity PRIMARY KEY (id));

CREATE TABLE federated_identity (identity_provider CHARACTER VARYING(255) NOT NULL, federated_user_id CHARACTER VARYING(255), federated_username CHARACTER VARYING(255), token TEXT, user_id UUID NOT NULL, CONSTRAINT pk_federated_identity PRIMARY KEY (identity_provider, user_id), CONSTRAINT fk_federated_identity_user FOREIGN KEY (user_id) REFERENCES "user_entity" ("id") ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE credential (id UUID DEFAULT gen_random_uuid() NOT NULL, device CHARACTER VARYING(255), hash_iterations INTEGER DEFAULT 27500 NOT Null, salt BYTEA, type CHARACTER VARYING(255), value CHARACTER VARYING(4000), user_id UUID, created_date TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT now(), counter INTEGER DEFAULT 0, digits INTEGER DEFAULT 6, period INTEGER DEFAULT 30, algorithm CHARACTER VARYING(36) DEFAULT "Blowfish" NOT NULL, CONSTRAINT pk_credential PRIMARY KEY (id), CONSTRAINT fk_credential_user FOREIGN KEY (user_id) REFERENCES "user_entity" ("id") ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE user_required_action (user_id UUID NOT NULL, required_action CHARACTER VARYING(255) DEFAULT ' '::character varying NOT NULL, CONSTRAINT pk_required_action PRIMARY KEY (required_action, user_id), CONSTRAINT user_required_action_user FOREIGN KEY (user_id) REFERENCES "user_entity" ("id") ON DELETE CASCADE ON UPDATE CASCADE);


DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
