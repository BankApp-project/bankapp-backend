-- -- 1. Enum dla typu transakcji
-- CREATE TYPE transaction_type AS ENUM (
--     'DEPOSIT',
--     'FEE',
--     'TRANSFER_EXTERNAL',
--     'TRANSFER_INTERNAL',
--     'TRANSFER_OWN',
--     'WITHDRAWAL'
-- );

-- 2. Tabela admins
CREATE TABLE admins (
    id                  bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    credentials_expired boolean      NOT NULL,
    enabled             boolean      NOT NULL,
    expired             boolean      NOT NULL,
    locked              boolean      NOT NULL,
    password            varchar(255) NOT NULL,
    username            varchar(255) NOT NULL,
    CONSTRAINT UK5gr02hdrjhbm2sh88og0t7ic6 UNIQUE (username)
);

-- 3. Tabela admin_roles
CREATE TABLE admin_roles (
    admin_id bigint       NOT NULL,
    role     varchar(255) NULL,
    CONSTRAINT FKos10nu865i674o95ba9m5v1bg
        FOREIGN KEY (admin_id) REFERENCES admins (id)
        ON DELETE CASCADE
);

-- 4. Tabela password_reset_tokens
CREATE TABLE password_reset_tokens (
    id            bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    expires_at    timestamp(6)  NOT NULL,
    used          boolean       NOT NULL,
    used_at       timestamp(6)  NULL,
    user_email    varchar(255)  NOT NULL,
    user_fullname varchar(255)  NOT NULL,
    token_hash    varchar(255)  NOT NULL,
    CONSTRAINT UKeouy94p9xchcrat7pyvrsnfdq UNIQUE (token_hash)
);

-- 5. Tabela users
CREATE TABLE users (
    id                  integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pesel               varchar(255) NULL,
    date_of_birth       date         NULL,
    lastname            varchar(255) NULL,
    firstname           varchar(255) NULL,
    email               varchar(255) NULL,
    password            varchar(255) NULL,
    expired             boolean      NOT NULL DEFAULT FALSE,
    locked              boolean      NOT NULL DEFAULT FALSE,
    enabled             boolean      NOT NULL DEFAULT TRUE,
    credentials_expired boolean      NOT NULL,
    username            varchar(255) NOT NULL,
    account_counter     integer      NULL,
    phone_number        varchar(255) NOT NULL,
    CONSTRAINT UKitp610ijy6ku9afo2it8wajii UNIQUE (pesel)
);

-- 6. Tabela accounts
CREATE TABLE accounts (
    id                  integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    balance             decimal(38,2) NULL,
    owner_id            integer       NULL,
    creation_date       timestamp(6)  NULL,
    iban                varchar(255)  NOT NULL,
    user_account_number integer       NOT NULL,
    available_balance   decimal(38,2) NULL,
    CONSTRAINT FKjln86358moqf5k5pw89oiq8ur
        FOREIGN KEY (owner_id) REFERENCES users (id)
        ON DELETE CASCADE
);

-- 7. Tabela transactions
CREATE TABLE transactions (
    id             integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    amount         decimal(38,2)       NULL,
    date           timestamp(6)        NULL,
    status         varchar(20)         NULL,
    title          varchar(100)        NULL,
    type           varchar(20)         NULL,
    destination_id integer             NULL,
    source_id      integer             NULL,
    CONSTRAINT FK4030424h0rpxm0q0tnl74pbkh
        FOREIGN KEY (destination_id) REFERENCES accounts (id),
    CONSTRAINT FKnkrduafehebfdd3udxvp2c13r
        FOREIGN KEY (source_id) REFERENCES accounts (id)
);

-- 8. Tabela user_roles
CREATE TABLE user_roles (
    user_id integer      NOT NULL,
    role    varchar(255) NULL,
    CONSTRAINT FK7ov27fyo7ebsvada1ej7qkphl
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);
