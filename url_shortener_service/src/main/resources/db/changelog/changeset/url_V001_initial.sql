-- V1__Create_url_sequence.sql
CREATE SEQUENCE IF NOT EXISTS url_sequence
    START WITH 916132832
    INCREMENT BY 1
    NO MAXVALUE
    NO CYCLE
    CACHE 1;

-- V2__Create_hash_table.sql
DROP TABLE IF EXISTS hash;
CREATE TABLE hash (
            id BIGSERIAL PRIMARY KEY,
            value VARCHAR(7) NOT NULL,
            used BOOLEAN NOT NULL DEFAULT FALSE,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT hash_value_unique UNIQUE (value)
);

CREATE INDEX IF NOT EXISTS idx_hash_used ON hash(used) WHERE NOT used;

ALTER SEQUENCE url_sequence OWNER TO current_user;
ALTER TABLE hash OWNER TO current_user;

-- V3__Create_url_table.sql
CREATE TABLE IF NOT EXISTS url (
            id BIGSERIAL PRIMARY KEY,
            hash_value VARCHAR(7) NOT NULL REFERENCES hash(value),
            original_url TEXT NOT NULL,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
            expires_at TIMESTAMP WITH TIME ZONE,
            visits_count BIGINT DEFAULT 0,
            CONSTRAINT url_hash_value_unique UNIQUE (hash_value)
            );

CREATE INDEX IF NOT EXISTS idx_url_created_at ON url(created_at);
CREATE INDEX IF NOT EXISTS idx_url_expires_at ON url(expires_at);

-- V4__Add_url_config_table.sql
CREATE TABLE IF NOT EXISTS url_config (
            id BIGSERIAL PRIMARY KEY,
            config_key VARCHAR(50) NOT NULL,
            config_value VARCHAR(255) NOT NULL,
            description TEXT,
            updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT url_config_key_unique UNIQUE (config_key)
    );

INSERT INTO url_config (config_key, config_value, description) VALUES
           ('default_expiration_days', '30', 'Default number of days before URL expiration'),
           ('max_url_length', '2048', 'Maximum allowed length for original URL'),
            ('min_hash_length', '5', 'Minimum length for generated hash'),
            ('max_hash_length', '7', 'Maximum length for generated hash');

-- V5__Add_audit_table.sql
CREATE TABLE IF NOT EXISTS url_audit (
            id BIGSERIAL PRIMARY KEY,
            url_hash_value VARCHAR(7) NOT NULL REFERENCES hash(value),
            action VARCHAR(50) NOT NULL,
            ip_address VARCHAR(45),
            user_agent TEXT,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

CREATE INDEX IF NOT EXISTS idx_audit_url_hash_value ON url_audit(url_hash_value);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON url_audit(created_at);